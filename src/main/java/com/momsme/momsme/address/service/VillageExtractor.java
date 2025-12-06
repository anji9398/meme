package com.momsme.momsme.address.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class VillageExtractor {

    private Set<String> villageSet = new HashSet<>();

    public VillageExtractor(String villagesJsonFromDb) {
        JSONObject root = new JSONObject(villagesJsonFromDb);
        JSONArray mandalsArr = root.getJSONArray("mandals");
        for (int i = 0; i < mandalsArr.length(); i++) {
            JSONArray villagesArr = mandalsArr.getJSONObject(i).getJSONArray("villages");
            for (int j = 0; j < villagesArr.length(); j++) {
                String vname = villagesArr.getJSONObject(j).getString("villageName");
                villageSet.add(normalize(vname)); // normalize() available here
            }
        }
    }
    // ⭐ STAR: extractVillageOpt1 logic from your old extractor (unchanged)
    public String extractVillageOpt1(String address) {
        if (address == null || address.trim().isEmpty()) return null;

        Set<String> found = new LinkedHashSet<>();

        String cleaned = normalize(address);
        String[] tokens = cleaned.split(" ");

        Set<String> ignoreWords = Set.of(
                "galli", "street", "road", "block", "building", "flat",
                "town", "city", "circle", "area", "lane", "colony"
        );

        for (int i = 0; i < tokens.length; i++) {

            // 3-word check
            if (i + 2 < tokens.length) {
                String w3 = tokens[i] + " " + tokens[i + 1] + " " + tokens[i + 2];
                if (matchVillageScore(w3) >= 0.90) found.add(w3);
            }

            // 2-word check
            if (i + 1 < tokens.length) {
                String w2 = tokens[i] + " " + tokens[i + 1];
                if (matchVillageScore(w2) >= 0.90) found.add(w2);
            }

            // 1-word check
            String w1 = tokens[i];
            if (!ignoreWords.contains(w1) && matchVillageScore(w1) >= 0.90) {
                found.add(w1);
            }
        }

        // Remove duplicates / fuzzy merge
        List<String> cleanedList = new ArrayList<>();
        for (String v : found) {
            boolean merged = false;

            for (int i = 0; i < cleanedList.size(); i++) {
                String existing = cleanedList.get(i);
                double score = similarity(v, existing);

                if (score >= 0.80) {
                    merged = true;
                    if (v.length() > existing.length()) cleanedList.set(i, v);
                    break;
                }
            }

            if (!merged) cleanedList.add(v);
        }

        found = new LinkedHashSet<>(cleanedList);

        // Final result
        if (found.size() > 1) return "Multiple villages detected: " + String.join(", ", found);
        if (found.size() == 1) return found.iterator().next();
        return null;
    }

    // 🔧 Utility methods (existing)
    private String normalize(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private double matchVillageScore(String input) {
        if (villageSet.contains(input)) return 1.0;
        double best = 0;
        for (String v : villageSet) best = Math.max(best, similarity(input, v));
        return best;
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
}

