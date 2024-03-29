import { UiNode, UiNodeAttributes } from "@ory/kratos-client";
import { computed, type PropType, ref } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import { type ValidationRule } from "@vuelidate/core";
import { minLength, required, email } from "~/lib/composables/useValidationHelpers";

export const formProps = () => ({
  node: {
    type: Object as PropType<UiNode>,
    required: true,
  },
  disabledField: {
    type: Boolean,
    default: false,
  },
  disableAutocomplete: {
    type: Boolean,
    default: false,
  },
});

export interface Props {
  node: UiNode;
  disabledField: boolean;
  disableAutocomplete: boolean;
}

export function useFormElement<T extends UiNodeAttributes>(props: Props) {
  const errorMessages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type === "error").map((m) => m.text);
  });

  const messages = computed<string[]>(() => {
    return props.node.messages.filter((m) => m.type !== "error").map((m) => m.text);
  });

  const value = ref<string>((props.node.attributes as UiNodeInputAttributes).value);

  const rules: ValidationRule<string | undefined>[] = [];
  if ((props.node.attributes as UiNodeInputAttributes).required) {
    rules.push(required());
  }
  if ((props.node.attributes as UiNodeInputAttributes).type === "password") {
    rules.push(minLength()(8));
  }
  if ((props.node.attributes as UiNodeInputAttributes).type === "email") {
    rules.push(email());
  }

  return {
    errorMessages,
    messages,
    node: props.node as Omit<UiNode, "attributes"> & { attributes: T },
    disabledField: props.disabledField,
    disableAutocomplete: props.disableAutocomplete,
    value,
    rules,
  };
}
