FROM node:18.15.0 AS builder

WORKDIR /usr/src/app

COPY package.json package-lock.json ./

RUN npm config set registry https://registry.npmmirror.com

RUN npm install

COPY . ./

FROM nginx:1.21.4

ENV TZ Asia/Shanghai

COPY --from=builder /usr/src/app/dist /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
