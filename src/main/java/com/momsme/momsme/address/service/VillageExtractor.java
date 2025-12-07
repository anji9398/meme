package com.momsme.momsme.address.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class VillageExtractor {

    private final Set<String> villageSet = new HashSet<>();
    private final Set<String> mandalSet = new HashSet<>();
    private final Map<String, String> normalizedToOriginal = new HashMap<>();
    private final Map<String, Set<String>> mandalToVillages = new HashMap<>();
    private final String originalJson;

    public VillageExtractor(String json) {
        this.originalJson = json;

        JSONObject root = new JSONObject(json);
        JSONArray mandalsArr = root.getJSONArray("mandals");

        for (int i = 0; i < mandalsArr.length(); i++) {
            JSONObject mandalObj = mandalsArr.getJSONObject(i);
            String mandalName = mandalObj.getString("mandalName");
            String mandalNorm = normalize(mandalName);
            mandalSet.add(mandalNorm);

            JSONArray villagesArr = mandalObj.getJSONArray("villages");
            Set<String> vset = new LinkedHashSet<>();
            for (int j = 0; j < villagesArr.length(); j++) {
                String vname = villagesArr.getJSONObject(j).getString("villageName");
                String vnorm = normalize(vname);
                vset.add(vnorm);
                villageSet.add(vnorm);
                normalizedToOriginal.putIfAbsent(vnorm, vname);
            }
            mandalToVillages.put(mandalNorm, vset);
        }
    }

    private String formatConflict(Collection<String> normalizedVillages) {
        List<String> originals = new ArrayList<>();
        for (String n : normalizedVillages) {
            String orig = normalizedToOriginal.getOrDefault(n, n);
            originals.add(orig);
        }
        return "village_conflict:" + String.join(", ", originals);
    }

    /* ---------------------------------------------------------
       🔥 Detect Mandal from Address (Primary method to call)
       --------------------------------------------------------- */
    public String detectMandal(String address) {
        if (address == null) return null;
        return extractMandalOpt1(address);
    }

    /* ----------------------------------------------------------
       🔥 Detect Village inside detected Mandal (main method)
       ---------------------------------------------------------- */
    public String detectVillage(String address, String mandalName) {
        if (address == null || mandalName == null) return null;
        // convenience wrapper: use extractVillageWithinMandal
        return extractVillageWithinMandal(address);
    }

    /* ------------------------------------------------
       Mandal Extraction (unchanged logic)
       ------------------------------------------------ */
    public String extractMandalOpt1(String address) {
        if (address == null || address.trim().isEmpty()) return null;

        String cleaned = normalize(address);
        String[] tokens = cleaned.split(" ");

        String bestMatch = null;
        double bestScore = 0.0;

        for (String mandal : mandalSet) {

            // direct substring match
            if (cleaned.contains(mandal)) {
                return mandal; // highest confidence
            }

            // token-based max similarity
            for (int i = 0; i < tokens.length; i++) {

                String w1 = tokens[i];
                double score1 = similarity(w1, mandal);
                if (score1 > bestScore) {
                    bestScore = score1;
                    bestMatch = mandal;
                }

                if (i + 1 < tokens.length) {
                    String w2 = tokens[i] + " " + tokens[i + 1];
                    double score2 = similarity(w2, mandal);
                    if (score2 > bestScore) {
                        bestScore = score2;
                        bestMatch = mandal;
                    }
                }

                if (i + 2 < tokens.length) {
                    String w3 = tokens[i] + " " + tokens[i + 1] + " " + tokens[i + 2];
                    double score3 = similarity(w3, mandal);
                    if (score3 > bestScore) {
                        bestScore = score3;
                        bestMatch = mandal;
                    }
                }
            }
        }

        // Final threshold check
        return (bestMatch != null && bestScore >= 0.90) ? bestMatch : null;
    }

    /* ------------------------------------------------
       Utility Methods
       ------------------------------------------------ */
    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replaceAll("[^a-z]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private double similarity(String s1, String s2) {
        int dist = levenshtein(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) dist / maxLen);
    }

    private int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++)
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        return dp[s1.length()][s2.length()];
    }

    /* ------------------------------------------------
       Get villages by mandal (unchanged)
       ------------------------------------------------ */
    public Set<String> getVillagesByMandal(String mandalName) {
        if (mandalName == null) return Set.of();
        return mandalToVillages.getOrDefault(normalize(mandalName), Set.of());
    }

    public String extractVillageWithinMandal(String address) {

        if (address == null || address.trim().isEmpty()) return "village_not_found";

        String mandal = extractMandalOpt1(address);
        if (mandal == null) return "mandal_not_found";

        Set<String> villagesInMandal = getVillagesByMandal(mandal);
        if (villagesInMandal.isEmpty()) return "village_not_found";

        String cleaned = normalize(address);
        String[] tokens = cleaned.split(" ");

        // build variants for each village (normalized)
        Map<String, Set<String>> variants = new LinkedHashMap<>();
        for (String v : villagesInMandal) {
            Set<String> vs = new LinkedHashSet<>();
            String base = v.replaceAll("\\s+", " ");
            vs.add(base);
            vs.add(base.replaceAll("\\([^)]*\\)", "").trim());
            vs.add(base.replace("-", " ").trim());
            vs.add(base.replace("_", " ").trim());
            vs.add(base.replaceAll("\\s+", " ").trim());
            variants.put(v, vs);
        }

        // 1) exact substring match
        Set<String> exactMatches = new LinkedHashSet<>();
        for (Map.Entry<String, Set<String>> e : variants.entrySet()) {
            String vnorm = e.getKey();
            for (String var : e.getValue()) {
                if (var.isEmpty()) continue;
                if (cleaned.contains(var)) {
                    exactMatches.add(vnorm);
                    break;
                }
            }
        }

        if (exactMatches.size() == 1) return normalizedToOriginal.getOrDefault(exactMatches.iterator().next(), exactMatches.iterator().next());
        if (exactMatches.size() > 1) return formatConflict(exactMatches);

        // QUICK STEM check (leading token)
        Map<String, Set<String>> leadingMap = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> e : variants.entrySet()) {
            String vnorm = e.getKey();
            for (String var : e.getValue()) {
                if (var == null || var.isEmpty()) continue;
                String[] vtoks = var.split(" ");
                if (vtoks.length == 0) continue;
                String lead = vtoks[0];
                leadingMap.computeIfAbsent(lead, k -> new LinkedHashSet<>()).add(vnorm);
            }
        }

        for (Map.Entry<String, Set<String>> e : leadingMap.entrySet()) {
            String lead = e.getKey();
            Set<String> group = e.getValue();
            boolean matched = false;
            for (String t : tokens) {
                if (t == null || t.isEmpty()) continue;
                double sim = similarity(t, lead);
                if (t.contains(lead) || lead.contains(t) || t.startsWith(lead) || lead.startsWith(t) || sim >= 0.78) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                if (group.size() > 1) return formatConflict(group);
                if (group.size() == 1) return normalizedToOriginal.getOrDefault(group.iterator().next(), group.iterator().next());
            }
        }

        // 2) context window around mandal
        String nm = normalize(mandal);
        int mandalIndex = -1;
        for (int i = 0; i < tokens.length; i++) if (tokens[i].contains(nm)) { mandalIndex = i; break; }

        int start = 0, end = tokens.length;
        if (mandalIndex != -1) { start = Math.max(0, mandalIndex - 3); end = Math.min(tokens.length, mandalIndex + 6); }
        else { start = 0; end = Math.min(tokens.length, 8); }

        List<String> candidateWords = new ArrayList<>();
        for (int i = start; i < end; i++) candidateWords.add(tokens[i]);
        String context = String.join(" ", candidateWords);

        // n-grams
        List<String> ngrams = new ArrayList<>();
        String[] ctxTokens = context.split(" ");
        for (int i = 0; i < ctxTokens.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int l = 1; l <= 4 && i + l <= ctxTokens.length; l++) {
                if (l > 1) sb.append(" ");
                sb.append(ctxTokens[i + l - 1]);
                ngrams.add(sb.toString());
            }
        }
        if (ngrams.isEmpty()) ngrams.add(context);

        double bestScore = 0.0;
        String bestVillage = null;
        Map<String, Double> topScores = new LinkedHashMap<>();

        for (String v : variants.keySet()) {
            double localBest = 0.0;
            for (String var : variants.get(v)) {
                if (var == null || var.isEmpty()) continue;
                localBest = Math.max(localBest, similarity(context, var));
                for (String ng : ngrams) localBest = Math.max(localBest, similarity(ng, var));
            }
            topScores.put(v, localBest);
            if (localBest > bestScore) { bestScore = localBest; bestVillage = v; }
        }

        double threshold = 0.90;
        if (bestVillage != null && bestVillage.length() <= 3 && bestScore < 0.99) return "village_not_found";
        if (bestScore < threshold) return "village_not_found";

        Set<String> tied = new LinkedHashSet<>();
        for (Map.Entry<String, Double> en : topScores.entrySet()) {
            String v = en.getKey();
            Double sc = en.getValue();
            if (sc >= threshold) tied.add(v);
            else if (sc + 0.02 >= bestScore && sc >= (threshold - 0.02)) tied.add(v);
        }

        if (tied.size() > 1) return formatConflict(tied);
        if (tied.size() == 1) return normalizedToOriginal.getOrDefault(tied.iterator().next(), tied.iterator().next());

        return normalizedToOriginal.getOrDefault(bestVillage, bestVillage);
    }

}
