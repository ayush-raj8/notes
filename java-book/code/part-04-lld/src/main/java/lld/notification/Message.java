package lld.notification;

/** Rendered content, independent of channel. Senders adapt it (SMS drops the subject). */
public record Message(String subject, String body) {}
