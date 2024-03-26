package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetNameCounter {
    private final List<Map<String, Object>> pets;

    public PetNameCounter(List<Map<String, Object>> pets) {
        this.pets = pets;
    }

    public Map<String, Integer> countPetNames() {
        Map<String, Integer> nameCounts = new HashMap<>();

        for (Map<String, Object> pet : pets) {
            String name = (String) pet.get("name");
            nameCounts.put(name, nameCounts.getOrDefault(name, 0) + 1);
        }

        return nameCounts;
    }
}