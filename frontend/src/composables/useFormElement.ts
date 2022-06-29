import { UiNode } from "@ory/kratos-client";
import { computed, PropType } from "vue";
import { Props } from "~/types/helpers";

export const formProps = () => ({
  node: {
    type: Object as PropType<UiNode>,
    required: true,
  },
  disabledField: {
    type: Boolean,
    default: false,
  },
  disabledAutocomplete: {
    type: Boolean,
    default: false,
  },
});

export function useFormElement(props: Props) {
  const errorMessages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type === "error").map((m) => m.text);
  });

  const messages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type !== "error").map((m) => m.text);
  });

  return { errorMessages, messages, node: props.node, disabledField: props.disabledField, disabledAutocomplete: props.disabledAutocomplete };
}
