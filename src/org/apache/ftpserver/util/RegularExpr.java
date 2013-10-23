package org.apache.ftpserver.util;

public class RegularExpr {

    private char[] pattern;

    public RegularExpr(String pattern) {
        this.pattern = pattern.toCharArray();
    }

    public boolean isMatch(String name) {
        if ((pattern.length == 1) && (pattern[0] == '*')) {
            return true;
        }
        return isMatch(name.toCharArray(), 0, 0);
    }

    private boolean isMatch(char[] strName, int strIndex, int patternIndex) {
        while (true) {
            if (patternIndex >= pattern.length) {
                return strIndex == strName.length;
            }
            char pc = pattern[patternIndex++];
            switch(pc) {
                case '[':
                    if (strIndex >= strName.length) {
                        return false;
                    }
                    char fc = strName[strIndex++];
                    char lastc = 0;
                    boolean bMatch = false;
                    boolean bNegete = false;
                    boolean bFirst = true;
                    while (true) {
                        if (patternIndex >= pattern.length) {
                            return false;
                        }
                        pc = pattern[patternIndex++];
                        if (pc == ']') {
                            if (bFirst) {
                                bMatch = true;
                            }
                            break;
                        }
                        if (bMatch) {
                            continue;
                        }
                        if ((pc == '^') && bFirst) {
                            bNegete = true;
                            continue;
                        }
                        bFirst = false;
                        if (pc == '-') {
                            if (patternIndex >= pattern.length) {
                                return false;
                            }
                            pc = pattern[patternIndex++];
                            bMatch = (fc >= lastc) && (fc <= pc);
                            lastc = pc;
                        } else {
                            lastc = pc;
                            bMatch = (pc == fc);
                        }
                    }
                    if (bNegete) {
                        if (bMatch) {
                            return false;
                        }
                    } else {
                        if (!bMatch) {
                            return false;
                        }
                    }
                    break;
                case '*':
                    if (patternIndex >= pattern.length) {
                        return true;
                    }
                    do {
                        if (isMatch(strName, strIndex++, patternIndex)) {
                            return true;
                        }
                    } while (strIndex < strName.length);
                    return false;
                case '?':
                    if (strIndex >= strName.length) {
                        return false;
                    }
                    strIndex++;
                    break;
                default:
                    if (strIndex >= strName.length) {
                        return false;
                    }
                    if (strName[strIndex++] != pc) {
                        return false;
                    }
                    break;
            }
        }
    }
}
