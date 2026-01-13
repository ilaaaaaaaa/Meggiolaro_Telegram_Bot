package org.example;

import java.util.List;

public class House {

    private String slug;
    private String name;
    private List<HouseMember> members;

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public List<HouseMember> getMembers() {
        return members;
    }
}

