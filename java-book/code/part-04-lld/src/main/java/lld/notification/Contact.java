package lld.notification;

import java.util.Optional;

/** Where a user can be reached. Any address may be null (not registered). */
public record Contact(String email, String phone, String deviceToken) {
  public Optional<String> addressFor(Channel channel) {
    String address =
        switch (channel) {
          case EMAIL -> email;
          case SMS -> phone;
          case PUSH -> deviceToken;
        };
    return Optional.ofNullable(address);
  }
}
