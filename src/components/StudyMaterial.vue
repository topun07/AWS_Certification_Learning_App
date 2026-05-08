<template>
  <div class="max-w-5xl mx-auto py-12 px-4 animate-fade-in">

    <div class="mb-8 border-b border-slate-700 pb-6">
      <h1 class="text-3xl font-black text-white tracking-widest uppercase mb-2">AIF-C01: Fundamentals of AI</h1>
      <p class="text-slate-400">Official Knowledge Forge Training Matrix</p>
    </div>

    <div class="bg-slate-900 border border-slate-700 rounded-2xl p-8 md:p-12 shadow-2xl">
      <div
          class="prose prose-invert prose-indigo prose-headings:font-black prose-a:text-indigo-400 max-w-none"
          v-html="secureRenderedHtml"
      ></div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // To read the URL
import { marked } from 'marked';
import DOMPurify from 'dompurify';

const route = useRoute();
const documentTitle = ref('');
const rawMarkdown = ref('');

// When the page loads, fetch the specific document from Java!
onMounted(async () => {
  const cert = route.params.certCode; // e.g., "AIF-C01"
  const domain = route.params.domain; // e.g., "Fundamentals"

  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/study/${cert}/${domain}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.ok) {
      const data = await response.json();
      documentTitle.value = data.title;
      rawMarkdown.value = data.content; // The raw AI-generated markdown!
    }
  } catch (error) {
    console.error("Failed to load matrix archives.");
  }
});

</script>