import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  test: {                // Use curly braces {} here, not []
    environment: 'jsdom',
    globals: true
  }                      // Remove the semicolon that was here
})