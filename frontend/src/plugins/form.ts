import { defineNuxtPlugin } from "#imports";
import FormInputPassword from "~/components/form/FormInputPassword.vue";
import FormInputText from "~/components/form/FormInputText.vue";
import FormInputHidden from "~/components/form/FormInputHidden.vue";
import FormInputEmail from "~/components/form/FormInputEmail.vue";
import FormInputSubmit from "~/components/form/FormInputSubmit.vue";
import FormInputButton from "~/components/form/FormInputButton.vue";
import FormImg from "~/components/form/FormImg.vue";
import FormText from "~/components/form/FormText.vue";
import FormScript from "~/components/form/FormScript.vue";

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.component("FormInputPassword", FormInputPassword);
  nuxtApp.vueApp.component("FormInputText", FormInputText);
  nuxtApp.vueApp.component("FormInputHidden", FormInputHidden);
  nuxtApp.vueApp.component("FormInputEmail", FormInputEmail);
  nuxtApp.vueApp.component("FormInputSubmit", FormInputSubmit);
  nuxtApp.vueApp.component("FormInputButton", FormInputButton);
  nuxtApp.vueApp.component("FormImg", FormImg);
  nuxtApp.vueApp.component("FormText", FormText);
  nuxtApp.vueApp.component("FormScript", FormScript);
});
