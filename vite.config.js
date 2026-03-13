import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite' // Add this line

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(), // Add this line
  ],
})