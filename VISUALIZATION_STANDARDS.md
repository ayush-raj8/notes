# Visualization standards

Interactive diagrams are for mechanisms whose intermediate state is the lesson. A table, a static figure, or a short step list is enough when the state does not change under the reader's control.

## Closed set

The allowed ids live in `src/content/visualContract.ts`. A lesson can reference only those ids. Add an id only with a written learning objective. Implement it by adding a pure model under `src/visuals/models`, a view under `src/visuals/views`, a registry entry, and a model test.

A `complete` lesson cannot reference a visual that is not implemented. A draft can. The reader must still show the learning objective and caption when the view is missing, so the page is not an empty box.

## Behavior

- State the learning objective next to the diagram.
- Label directions, sequence numbers, and addresses in text, not by color alone.
- Expose the step list to the keyboard: previous, next, and a control that jumps to a named step when there are more than a few.
- Show the intermediate state, not only the start and end.
- Include a text description of the current step that updates with the step.
- Respect `prefers-reduced-motion`. Autoplay is off.
- Do not simplify a protocol into a false state transition. If a teaching scenario omits a field, say so in the caption.

The TCP timeline is the reference implementation for later diagrams. Handshake and teardown scenarios must show who sent SYN, SYN-ACK, ACK, FIN, or RST, the sequence and acknowledgment numbers that matter, and what each endpoint knows after the step.

## Tests

Model tests assert the state at each step. They do not snapshot SVG markup. A component test can check that the text alternative matches the model.
