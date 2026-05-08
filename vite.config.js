import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [
    vue(),
  ],
  test: {                // Use curly braces {} here, not []
    environment: 'jsdom',
    globals: true
  }                      // Remove the semicolon that was here
})