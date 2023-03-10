// simple dum mock
export const useBackendData = {
  validations: {
    project: {
      name: { regex: "^[a-zA-Z0-9-_]{3,}$", max: 25 },
      desc: { max: 120 },
      license: { regex: "^[a-zA-Z0-9-_.() +]*$", max: 15 },
      keywords: { max: 5 },
      channels: { regex: "^[a-zA-Z0-9]+$", max: 15, min: 1 },
      pageName: { regex: "^[a-zA-Z0-9-_ ]+$", max: 25, min: 3 },
      pageContent: { max: 75000, min: 15 },
      sponsorsContent: { max: 1500 },
      maxPageCount: 50,
      maxChannelCount: 5,
      maxFileSize: 10000000,
    },
    userTagline: { max: 100 },
    version: { regex: "^[a-zA-Z0-9-_.+]+$", max: 30 },
    org: { regex: "^[a-zA-Z0-9-_]+$", max: 20, min: 3 },
    maxOrgCount: 5,
    urlRegex:
      "^(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
  },
};
