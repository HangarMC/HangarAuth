import { UiNode } from "@ory/kratos-client";
import { computed, PropType, ref } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";

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

export interface Props {
  node: UiNode;
  disabledField: boolean;
  disabledAutocomplete: boolean;
}

export function useFormElement(props: Props) {
  const errorMessages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type === "error").map((m) => m.text);
  });

  const messages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type !== "error").map((m) => m.text);
  });

  const value = ref<string>((props.node.attributes as UiNodeInputAttributes).value);

  return {
    errorMessages,
    messages,
    node: props.node,
    disabledField: props.disabledField,
    disabledAutocomplete: props.disabledAutocomplete,
    value,
  };
}
