package com.momsme.momsme.address.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class VillageExtractor {

    /* ---------------- STOP WORDS --------------- */
    private static final Set<String> STOP_WORDS = Set.of(
            "flat","floor","no","dno","door","house","building","bldg","apartment","apt",
            "road","rd","street","st","lane","ln", "adilabad",
            "near","beside","behind","opp","opposite","bus","stand",
            "area","colony", "nagar",
            "village","vill","v","town","city",
            "block","blk","dist","district",
            "mandal","mdl","m"
    );

    /* ---------------- Village Info --------------- */
    static class VillageInfo {
        Integer villageId;
        String villageName;
        Integer mandalId;
        String mandalName;
        String villageNorm;

        VillageInfo(Integer vid, String vname, Integer mid, String mname) {
            this.villageId = vid;
            this.villageName = vname;
            this.mandalId = mid;
            this.mandalName = mname;
            this.villageNorm = normalize(vname);
        }
    }

    private final List<VillageInfo> allVillages = new ArrayList<>();

    /* ---------------- Constructor --------------- */
    public VillageExtractor(String json) {
        JSONObject root = new JSONObject(json.trim());
        JSONArray mandalsArr = root.getJSONArray("mandals");

        for (int i = 0; i < mandalsArr.length(); i++) {
            JSONObject mandalObj = mandalsArr.getJSONObject(i);
            Integer mandalId = mandalObj.getInt("mandalId");
            String mandalName = mandalObj.getString("mandalName");

            JSONArray villagesArr = mandalObj.getJSONArray("villages");
            for (int j = 0; j < villagesArr.length(); j++) {
                JSONObject vObj = villagesArr.getJSONObject(j);
                Integer villageId = vObj.getInt("villageId");
                String villageName = vObj.getString("villageName");
                allVillages.add(new VillageInfo(villageId, villageName, mandalId, mandalName));
            }
        }
    }

    /* ---------------- NORMALIZE + STOP WORD CLEANER --------------- */
    private static String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replaceAll("[^a-z]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String removeStopWords(String normalized) {
        List<String> kept = new ArrayList<>();
        for (String t : normalized.split(" ")) {
            if (!STOP_WORDS.contains(t)) kept.add(t);
        }
        return String.join(" ", kept).trim();
    }

    /* ---------------- MASTER MATCH FUNCTION --------------- */
    public VillageDetectionResult detect(String address) {
        String normalized = normalize(address);
        String cleaned = removeStopWords(normalized);

        System.out.println("\n====== INPUT DEBUG ======");
        System.out.println("Raw: " + address);
        System.out.println("Normalized: " + normalized);
        System.out.println("Stop-word cleaned: " + cleaned);

        if (cleaned.isEmpty())
            return new VillageDetectionResult("INVALID", List.of());

        VillageDetectionResult strict = strictMatch(cleaned);
        if (!strict.status().equals("NOT_FOUND")) return strict;

        VillageDetectionResult token = substringMatch(cleaned);
        if (!token.status().equals("NOT_FOUND")) return token;

        VillageDetectionResult fuzzy = fuzzyMatch(cleaned);
        if (!fuzzy.status().equals("NOT_FOUND")) return fuzzy;

        VillageDetectionResult phonetic = phoneticMatch(cleaned);
        if (!phonetic.status().equals("NOT_FOUND")) return phonetic;

        VillageDetectionResult recover = longNameRecovery(cleaned);
        if (!recover.status().equals("NOT_FOUND")) return recover;

        VillageDetectionResult syllable = syllableDetect(cleaned);
        if (!syllable.status().equals("NOT_FOUND")) return syllable;

        return new VillageDetectionResult("NOT_FOUND", List.of());
    }

    /* ---------------- MATCHING LAYERS ---------------- */

    private VillageDetectionResult strictMatch(String cleaned) {
        System.out.println("[STRICT] tokens = " + cleaned);
        Set<String> tokens = new HashSet<>(Arrays.asList(cleaned.split(" ")));

        List<VillageInfo> matches = new ArrayList<>();
        for (VillageInfo v : allVillages) if (tokens.contains(v.villageNorm)) matches.add(v);

        return buildResponse(matches, "EXACT_MATCH");
    }

    private VillageDetectionResult substringMatch(String cleaned) {
        System.out.println("[SUBSTRING]");
        String[] rawTokens = cleaned.split(" ");

        Set<String> inputTokens = new HashSet<>();
        for (String t : rawTokens) if (t.length() >= 3) inputTokens.add(t);

        List<VillageInfo> matches = new ArrayList<>();
        for (VillageInfo v : allVillages) {
            Set<String> vTokens = new HashSet<>();
            for (String t : v.villageNorm.split(" ")) if (t.length() >= 3) vTokens.add(t);
            if (!Collections.disjoint(inputTokens, vTokens)) matches.add(v);
        }
        return buildResponse(matches, "TOKEN_SUBSTRING_FUZZY");
    }

    private VillageDetectionResult syllableDetect(String cleaned) {
        System.out.println("[SYLLABLE]");
        String c = cleaned.replace(" ", "");
        List<VillageInfo> matches = new ArrayList<>();
        for (VillageInfo v : allVillages) {
            String vn = v.villageNorm.replace(" ", "");
            if (c.length() >= 6 && vn.length() >= 6) {
                String startC = c.substring(0, Math.min(5, c.length()));
                String startV = vn.substring(0, Math.min(5, vn.length()));
                String endC = c.substring(c.length() - 3);
                String endV = vn.substring(vn.length() - 3);
                if (startC.equals(startV) || endC.equals(endV)) matches.add(v);
            }
        }
        return buildResponse(matches, "SYLLABLE_FUZZY");
    }

    private VillageDetectionResult fuzzyMatch(String cleaned) {
        System.out.println("[FUZZY]");
        List<ScoredVillage> candidates = new ArrayList<>();

        for (VillageInfo v : allVillages) {
            double score = similarity(cleaned, v.villageNorm);
            if (score >= 0.90) candidates.add(new ScoredVillage(v, score));
        }

        if (candidates.isEmpty()) return new VillageDetectionResult("NOT_FOUND", List.of());
        candidates.sort((a, b) -> Double.compare(b.score, a.score));
        return success(candidates.get(0).village, "FUZZY_MATCH score=" + candidates.get(0).score);
    }

    private VillageDetectionResult phoneticMatch(String cleaned) {
        System.out.println("[PHONETIC]");
        String c = cleaned.replace(" ", "");
        List<ScoredVillage> candidates = new ArrayList<>();

        for (VillageInfo v : allVillages) {
            String vn = v.villageNorm.replace(" ", "");
            double lev = levenshteinRatio(c, vn);
            double soft = softPhonetic(c, vn);
            if (lev >= 0.85) candidates.add(new ScoredVillage(v, Math.max(lev, soft)));
        }

        if (candidates.isEmpty()) return new VillageDetectionResult("NOT_FOUND", List.of());
        candidates.sort((a, b) -> Double.compare(b.score, a.score));
        return success(candidates.get(0).village, "PHONETIC_FUZZY score=" + candidates.get(0).score);
    }

    private VillageDetectionResult longNameRecovery(String cleaned) {
        System.out.println("[LONG_NAME_RECOVERY]");
        String c = cleaned.replace(" ", "");
        List<ScoredVillage> candidates = new ArrayList<>();

        for (VillageInfo v : allVillages) {
            String vn = v.villageNorm.replace(" ", "");
            if (vn.length() >= 10 && c.length() >= 6) {
                double score = levenshteinRatio(c, vn);
                if (score >= 0.78) candidates.add(new ScoredVillage(v, score));
            }
        }

        if (candidates.isEmpty()) return new VillageDetectionResult("NOT_FOUND", List.of());
        candidates.sort((a, b) -> Double.compare(b.score, a.score));
        return success(candidates.get(0).village, "LONG_NAME_RECOVERY score=" + candidates.get(0).score);
    }

    /* ---------------- Response Helpers ---------------- */

    private VillageDetectionResult buildResponse(List<VillageInfo> list, String label) {
        if (list.isEmpty()) return new VillageDetectionResult("NOT_FOUND", List.of());
        List<VillageDetectionResult.Match> out = new ArrayList<>();
        for (VillageInfo v : list) out.add(match(v, label));
        if (out.size() > 1) return new VillageDetectionResult("CONFLICT", out);
        return new VillageDetectionResult("SUCCESS", out);
    }

    private VillageDetectionResult success(VillageInfo v, String label) {
        return new VillageDetectionResult("SUCCESS", List.of(match(v, label)));
    }

    private VillageDetectionResult.Match match(VillageInfo v, String label) {
        return new VillageDetectionResult.Match(
                v.mandalId,
                v.villageId,
                v.mandalName,
                v.villageName,
                label
        );
    }

    /* ---------------- Similarity Utils ---------------- */

    private static double similarity(String a, String b) {
        Set<String> A = new HashSet<>(Arrays.asList(a.split(" ")));
        Set<String> B = new HashSet<>(Arrays.asList(b.split(" ")));
        int common = 0;
        for (String s : A) if (B.contains(s)) common++;
        return (2.0 * common) / (A.size() + B.size());
    }

    private static double levenshteinRatio(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        int dist = dp[a.length()][b.length()];
        return 1.0 - ((double) dist / Math.max(a.length(), b.length()));
    }

    private static double softPhonetic(String a, String b) {
        int matches = 0;
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) if (a.charAt(i) == b.charAt(i)) matches++;
        return (double) matches / Math.max(a.length(), b.length());
    }

    private static class ScoredVillage {
        VillageInfo village;
        double score;
        ScoredVillage(VillageInfo v, double s) { village = v; score = s; }
    }
}
