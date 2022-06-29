<script lang="ts" setup>
import { onBeforeUnmount, onMounted, ref, h } from "vue";
import { UiNodeScriptAttributes } from "@ory/kratos-client";
import { formProps, useFormElement } from "~/composables/useFormElement";
import { Props } from "~/types/helpers";

const script = ref();

const props = defineProps(formProps());
const { node } = useFormElement(props as Props);

onMounted(() => {
  const attributes = node.attributes as UiNodeScriptAttributes;
  script.value = document.createElement("script");

  script.value.async = true;
  script.value.src = attributes.src;
  script.value.async = attributes.async;
  script.value.crossOrigin = attributes.crossorigin;
  script.value.integrity = attributes.integrity;
  script.value.referrerPolicy = attributes.referrerpolicy;
  script.value.type = attributes.type;

  document.body.append(script.value);
  console.log("script mounted", script.value);
});

onBeforeUnmount(() => {
  if (script.value) {
    document.body.removeChild(script.value);
  }
});

// return (h: any) => h();
</script>
