import { defineEventHandler } from "h3";

export default defineEventHandler((event) => {
  const origin = event.node.req.headers.origin;
  if (origin && ["http://localhost:3333", "https://hangar.benndorf.dev", "https://hangar.papermc.io", "https://hangar.papermc.dev"].includes(origin)) {
    event.node.res.setHeader("Access-Control-Allow-Origin", origin);
    event.node.res.setHeader("Access-Control-Allow-Credentials", "true");
  }
});
