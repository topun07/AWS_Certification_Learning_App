<template>
  <div class="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8 font-sans">
    <div class="max-w-4xl 2xl:max-w-6xl mx-auto animate-fade-in">

      <div class="mb-8">
        <button @click="emit('back')" class="text-indigo-600 hover:text-indigo-500 font-bold text-sm tracking-widest uppercase flex items-center gap-2 transition-colors">
          <span>←</span> Return to Knowledge Forge
        </button>
      </div>

      <div v-if="isLoading" class="text-center py-20">
        <div class="w-16 h-16 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin mx-auto mb-6"></div>
        <p class="text-indigo-600 font-bold tracking-widest uppercase animate-pulse">Loading Study Material...</p>
      </div>

      <div v-else-if="errorMessage" class="bg-red-50 border border-red-200 rounded-2xl p-8 text-center">
        <span class="text-4xl block mb-4">⚠️</span>
        <h3 class="text-xl font-black text-red-800 uppercase tracking-widest mb-2">Archive Not Found</h3>
        <p class="text-red-600">{{ errorMessage }}</p>
      </div>

      <div v-else class="bg-white border border-slate-200 rounded-3xl p-8 md:p-12 shadow-sm relative overflow-hidden">

        <div class="border-b border-slate-200 pb-8 mb-8 relative z-10">
          <span class="text-indigo-600 font-black tracking-widest uppercase text-sm bg-indigo-50 px-3 py-1 rounded-full border border-indigo-200 mb-4 inline-block">
            {{ document.certCode }} • {{ document.domain }}
          </span>
          <h1 class="text-3xl md:text-5xl font-black text-slate-900 mt-4 leading-tight">{{ document.title }}</h1>
        </div>

        <div
            class="prose prose-slate prose-lg md:prose-xl prose-headings:font-black prose-h2:text-slate-900 prose-h3:text-slate-800 prose-a:text-indigo-600 prose-strong:text-indigo-700 prose-li:marker:text-indigo-500 max-w-none relative z-10 [&_table]:block [&_table]:overflow-x-auto [&_table]:whitespace-nowrap"
            v-html="secureRenderedHtml"
        ></div>

        <div class="absolute top-0 right-0 w-96 h-96 bg-indigo-50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

// 1. Accept the exact cert and domain from your main component
const props = defineProps({
  certCode: String,
  domain: String
});

// 2. Allow this component to tell the main page to "go back"
const emit = defineEmits(['back']);

const isLoading = ref(true);
const errorMessage = ref('');
const document = ref({});
const rawMarkdown = ref('');

// 3. Fetch the data using the props instead of a URL
onMounted(async () => {
  if (!props.certCode || !props.domain) return;
  try {
    const token = localStorage.getItem('aws_jwt');

    // 🚨 THE FIX: Wrap the variables in encodeURIComponent!
    const safeCert = encodeURIComponent(props.certCode);
    const safeDomain = encodeURIComponent(props.domain);

    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/study/${safeCert}/${safeDomain}`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    });

    if (response.ok) {
      const data = await response.json();
      document.value = data;
      rawMarkdown.value = data.content;
    } else {
      errorMessage.value = "The requested study material has not been injected into the Knowledge Forge yet.";
    }
  } catch (error) {
    errorMessage.value = "Critical server error. Could not connect to the database.";
  } finally {
    isLoading.value = false;
  }
});

const secureRenderedHtml = computed(() => {
  if (!rawMarkdown.value) return '';
  const rawHtml = marked.parse(rawMarkdown.value);
  return DOMPurify.sanitize(rawHtml);
});
</script>