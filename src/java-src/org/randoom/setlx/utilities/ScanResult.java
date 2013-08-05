package org.randoom.setlx.utilities;

/**
 * Result of a string-match, usually from inside the scan-statement.
 */
public class ScanResult extends MatchResult {

    private final int endOffset;

    /**
     * Create a new scan result, without any matching variables.
     *
     * @param matches   General result of the scan.
     * @param endOffset Offset of this scan.
     */
    public ScanResult(final boolean matches, final int endOffset) {
        super(matches);
        this.endOffset = endOffset;
    }

    /**
     * Create a new scan result from an existing MatchResult.
     *
     * @param result    Existing match result.
     * @param endOffset Offset of this match.
     */
    public ScanResult(final MatchResult result, final int endOffset) {
        this(result.isMatch(), endOffset);
        this.addBindings(result);
    }

    /**
     * Returns the offset of the last character matched.
     *
     * @return Offset of this match.
     */
    public int getEndOffset() {
        return endOffset;
    }
}

