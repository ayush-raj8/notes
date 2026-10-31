package solid.ocp;

import solid.ocp.sealed.Payment;

/** A sealed type is closed to extension from other files. */
public record OcpNotPermittedBroken(long cents) implements Payment {}
