package fit;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.


public class WikiRunner extends FileRunner {

    public static void main (String argv[]) {
        System.err.println("WikiRunner is deprecated: use FileRunner");
        FileRunner.main(argv);
    }
}
