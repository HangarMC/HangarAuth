import { UiNode } from "@ory/kratos-client";
import { computed, PropType, ref } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import { ValidationRule } from "@vuelidate/core";
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
    node: props.node,
    disabledField: props.disabledField,
    disabledAutocomplete: props.disabledAutocomplete,
    value,
    rules,
  };
}
