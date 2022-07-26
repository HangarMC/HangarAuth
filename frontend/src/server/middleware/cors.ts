import { defineEventHandler } from "h3";

export default defineEventHandler((event) => {
  const origin = event.req.headers.origin;
  if (origin && ["http://localhost:3333", "https://hangar.benndorf.dev"].includes(origin)) {
    event.res.setHeader('Access-Control-Allow-Origin', origin);
    event.res.setHeader('Access-Control-Allow-Credentials', 'true');
  }
});
