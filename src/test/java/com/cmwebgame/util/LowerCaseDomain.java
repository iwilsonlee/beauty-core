package com.cmwebgame.util;

import com.google.common.collect.DiscreteDomain;

public class LowerCaseDomain extends DiscreteDomain<Character> {
    private static LowerCaseDomain domain = new LowerCaseDomain();
 
    public static DiscreteDomain letters() {
        return domain;
    }
 
    @Override
    public Character next(Character c) {
        return (char) (c + 1);
    }
 
    @Override
    public Character previous(Character c) {
        return (char) (c - 1);
    }
 
    @Override
    public long distance(Character start, Character end) {
        return end - start;
    }
 
    @Override
    public Character maxValue() {
        return 'z';
    }
 
    @Override
    public Character minValue() {
        return 'a';
    }
}
