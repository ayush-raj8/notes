package solid.srp;

import solid.srp.parts.InvoiceRepository;

/** An implementation must honour the whole interface. */
public final class SrpRepoIncompleteBroken implements InvoiceRepository {
  @Override
  public void save(String id, long totalCents) {}
}
