<script setup lang="ts">
import { useI18n } from "vue-i18n";

const i18n = useI18n();
</script>

<template>
  <footer class="fixed bottom-0 flex items-end background-default px-8 pb-2 text-light-10 min-h-10">
    <div class="footerContent w-screen">
      <div class="footerBar flex flex-col gap-4 md:(flex-row-reverse gap-0) items-center justify-around mt-4 max-w-1200px m-auto text-sm">
        <div class="footerBarLinks flex flex-col md:flex-row">
          <a href="https://github.com/HangarMC" class="flex items-center rounded-md px-6 py-2" hover="text-primary-400 bg-primary-0">
            {{ i18n.t("footer.org") }}
          </a>
          <!-- todo tos link -->
          <a href="#" class="flex items-center rounded-md px-6 py-2" hover="text-primary-400 bg-primary-0">
            {{ i18n.t("footer.tos") }}
          </a>
        </div>
        <p class="font-bold">© {{ new Date().getFullYear() }} <a href="https://papermc.io/">PaperMC</a></p>
      </div>
    </div>
  </footer>
</template>
