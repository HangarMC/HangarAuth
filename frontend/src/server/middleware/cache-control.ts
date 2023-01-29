import { defineEventHandler } from "h3";

const STATICS_CACHE = 31536000; // 1 year

export default defineEventHandler((event) => {
  const url = event.node.req.url;
  if (url?.startsWith("/api") || !url?.match(/(.+)\.(jpg|jpeg|webp|gif|css|png|js|ico|svg|mjs)/)) return;
  event.node.res.setHeader("Cache-Control", `max-age=${STATICS_CACHE} s-maxage=${STATICS_CACHE}`);
});
