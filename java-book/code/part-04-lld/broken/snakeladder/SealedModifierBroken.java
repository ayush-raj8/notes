public class SealedModifierBroken {
  sealed interface Cell permits Plain {}

  class Plain implements Cell {}
}
