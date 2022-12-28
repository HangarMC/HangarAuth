<script setup lang="ts">
import { useI18n } from "vue-i18n";

const { t } = useI18n();
</script>

<template>
  <footer class="flex items-end mt-10 background-default px-8 pb-2 text-light-10 min-h-10">
    <div class="w-screen">
      <div class="flex flex-col gap-4 md:(flex-row-reverse gap-0) items-center justify-around mt-4 max-w-1200px m-auto text-sm">
        <div class="flex flex-col md:flex-row">
          <a href="https://github.com/HangarMC" class="flex items-center rounded-md px-6 py-2" hover="text-primary-400 bg-primary-0">
            {{ t("footer.org") }}
          </a>
          <a href="https://hangar.papermc.io/privacy" class="flex items-center rounded-md px-6 py-2" hover="text-primary-400 bg-primary-0">
            {{ t("footer.privacypolicy") }}
          </a>
        </div>
        <p class="font-bold">© {{ new Date().getFullYear() }} <a href="https://papermc.io/">PaperMC</a></p>
      </div>
    </div>
  </footer>
</template>
