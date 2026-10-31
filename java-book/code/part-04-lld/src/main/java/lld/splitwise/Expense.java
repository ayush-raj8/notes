package lld.splitwise;

// ANCHOR: expense
public record Expense(String id, String paidBy, long total, Split split) {}
// ANCHOR_END: expense
