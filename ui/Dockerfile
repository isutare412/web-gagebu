ARG PLATFORM=linux/amd64

FROM node:22-alpine AS builder

WORKDIR /app

RUN npm install -g pnpm
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

COPY src src
COPY static static
COPY .npmrc .npmrc
COPY package.json package.json
COPY pnpm-lock.yaml pnpm-lock.yaml
COPY svelte.config.js svelte.config.js
COPY tsconfig.json tsconfig.json
COPY vite.config.ts vite.config.ts

RUN pnpm run build

FROM --platform=${PLATFORM} nginx:alpine AS production

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /app/build /usr/share/nginx/html

ENTRYPOINT [ "nginx" ]
CMD [ "-g", "daemon off;"]
