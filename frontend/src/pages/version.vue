<script lang="ts" setup>
import { useHead } from "@vueuse/head";
import PageTitle from "~/lib/components/design/PageTitle.vue";
import Link from "~/lib/components/design/Link.vue";
import Card from "~/lib/components/design/Card.vue";

interface VersionInfo {
  time: string;
  commit: string;
  commitShort: string;
  version: string;
  committer: string;
  message: string;
  tag: string;
  behind: string;
}

const { data: version } = await useFetch<VersionInfo>("/version-info");
useHead({
  title: "Hangar Auth Version",
});
</script>

<template>
  <Card>
    <PageTitle>Hangar Version</PageTitle>
    <p mb="2">This instance is running <Link href="https://github.com/HangarMC/HangarAuth">HangarAuth</Link> {{ version.version }}</p>
    <p mb="2">
      The last commit was <Link :href="'https://github.com/HangarMC/HangarAuth/commit/' + version.commit">{{ version.commitShort }}</Link> by
      <Link :href="'https://github.com/' + version.committer">{{ version.committer }}</Link> at {{ version.time }} with message: <br />
      {{ version.message }}
    </p>
    <p mb="4">Last Tag: {{ version.tag }} ({{ version.behind || 0 }} commits since tag)</p>
  </Card>
</template>
