import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default () => {
  return defineConfig({
    base: "",
    plugins: [react()],
    server: {
      port: 8080,
    },
    build: {
      assetsInlineLimit: 0,
      target: "es2015",
    },
    resolve: {
      alias: {
        "@": "/src",
      },
    },
  });
};
