<template>
  <div class="min-h-screen bg-gray-50 py-12 px-4 font-sans">

    <div v-if="currentView === 'landing'" class="max-w-5xl mx-auto">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-extrabold text-gray-900 mb-4">AWS Certification Hub</h1>
        <p class="text-lg text-gray-600">Select your path and start your journey to AWS Mastery.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div
            v-for="cert in certifications"
            :key="cert.code"
            @click="selectCertification(cert)"
            class="group cursor-pointer bg-white rounded-3xl p-6 shadow-sm border border-gray-100 hover:shadow-xl hover:-translate-y-2 transition-all duration-300 flex flex-col h-full"
        >
          <div :class="['w-14 h-14 rounded-2xl bg-gradient-to-br flex items-center justify-center text-2xl mb-6 shadow-lg text-white', cert.color]">
            {{ cert.icon }}
          </div>
          <h3 class="text-xl font-bold text-gray-800 mb-2">{{ cert.name }}</h3>
          <p class="text-gray-400 text-xs font-mono mb-4 uppercase tracking-wider">{{ cert.code }}</p>
          <p class="text-gray-500 text-sm leading-relaxed mb-6 flex-grow">{{ cert.desc }}</p>
          <div class="flex items-center text-blue-600 font-bold text-sm group-hover:gap-2 transition-all">
            Start Practice ➔
          </div>
        </div>
      </div>

      <div class="mt-16">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-gray-800 tracking-tight">Recent Activity</h2>
          <span class="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full uppercase">Your Last 10 Attempts</span>
        </div>

        <div v-if="examHistory.length > 0" class="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
          <table class="w-full text-left border-collapse">
            <thead>
            <tr class="bg-gray-50 border-b border-gray-100">
              <th class="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest">Exam</th>
              <th class="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest">Date</th>
              <th class="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest text-center">Score</th>
              <th class="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest">Weakest Area</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
            <tr v-for="attempt in examHistory" :key="attempt.id" class="hover:bg-blue-50/30 transition-colors group">
              <td class="px-6 py-4">
                <span class="font-bold text-gray-700 group-hover:text-blue-600 transition-colors">{{ attempt.examCode }}</span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-500">
                {{ new Date(attempt.completedAt).toLocaleDateString() }}
              </td>
              <td class="px-6 py-4 text-center">
                <div :class="[
                    'inline-flex items-center justify-center w-12 h-12 rounded-xl font-black text-sm border-2',
                    attempt.scorePercentage >= 75 ? 'bg-green-50 text-green-700 border-green-100' : 'bg-red-50 text-red-700 border-red-100'
                  ]">
                  {{ attempt.scorePercentage }}%
                </div>
              </td>
              <td class="px-6 py-4">
                  <span class="text-xs font-bold bg-amber-50 text-amber-700 px-3 py-1 rounded-lg border border-amber-100">
                    {{ attempt.weakestCategory }}
                  </span>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="bg-white rounded-3xl p-12 text-center border-2 border-dashed border-gray-100">
          <div class="text-4xl mb-4">📊</div>
          <h3 class="text-lg font-bold text-gray-800">No activity yet</h3>
          <p class="text-gray-500">Complete your first practice exam to see your progress here.</p>
        </div>
      </div>
    </div>

    <div v-else class="max-w-2xl mx-auto">

      <button
          @click="goBackToLanding"
          class="mb-6 text-sm font-bold text-gray-400 hover:text-blue-600 transition-colors flex items-center gap-2 group"
      >
        <span class="group-hover:-translate-x-1 transition-transform">←</span> Switch Certification
      </button>

      <div v-if="showResults" class="bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-200 p-10 text-center">
        <div class="mb-4">
          <span :class="['px-6 py-2 rounded-full text-sm font-black uppercase tracking-widest border-2', Math.round((correctCount / totalExamQuestions) * 100) >= 75 ? 'bg-green-100 text-green-700 border-green-200' : 'bg-red-100 text-red-700 border-red-200']">
            {{ Math.round((correctCount / totalExamQuestions) * 100) >= 75 ? 'PASSED' : 'NEEDS IMPROVEMENT' }}
          </span>
        </div>
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Exam Results</h1>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-8 text-left">
          <div class="bg-blue-50 p-6 rounded-2xl text-center">
            <p class="text-blue-600 text-xs font-bold uppercase mb-1">Score</p>
            <p class="text-4xl font-black text-blue-900">{{ Math.round((correctCount / totalExamQuestions) * 100) }}%</p>
          </div>
          <div class="bg-amber-50 p-6 rounded-2xl">
            <p class="text-amber-600 text-xs font-bold uppercase mb-3">Study Priorities</p>
            <div v-for="p in getStudyPriorities()" :key="p.name" class="mb-2 text-[10px] font-bold text-amber-900">
              {{ p.name }}: {{ p.percentage }}%
            </div>
          </div>
        </div>
        <button @click="resetSession" class="w-full mt-8 bg-blue-600 text-white font-bold py-4 rounded-2xl">Start New Session</button>
      </div>

      <div v-else-if="question" class="bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-200">
        <div class="bg-blue-600 p-4 flex justify-between items-center">
          <div class="flex flex-col text-white">
            <h2 class="text-blue-200 uppercase tracking-widest text-[10px] font-bold">{{ question.category }}</h2>
            <span class="font-bold text-lg">Question {{ sessionCount }} of {{ totalExamQuestions }}</span>
          </div>
        </div>
        <div class="bg-gray-50 border-b border-gray-200 px-6 py-3 flex justify-between items-center">
          <select v-model="selectedCategory" @change="resetSession" class="bg-white border rounded px-2 py-1 text-sm outline-none">
            <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
          </select>
          <div class="font-mono font-bold text-blue-600">{{ formatTime(timer) }}</div>
        </div>
        <div class="p-8">
          <p class="text-xl font-medium text-gray-800 mb-8">{{ question.questionText }}</p>
          <div class="space-y-3">
            <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)" :disabled="showExplanation"
                    :class="['w-full text-left p-4 rounded-xl border-2 transition-all', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50' : 'border-gray-100 bg-gray-50', showExplanation && opt.isCorrect ? 'border-green-500 bg-green-50' : '']">
              {{ opt.optionText }}
            </button>
          </div>
          <button v-if="!showExplanation" @click="checkAnswer" :disabled="selectedAnswers.length === 0" class="w-full mt-8 bg-blue-600 text-white font-bold py-4 rounded-xl">Submit</button>
          <div v-if="showExplanation" class="mt-8">
            <div :class="['p-4 rounded-xl text-center font-bold mb-4', feedbackClass]">{{ feedback }}</div>
            <p class="bg-amber-50 p-4 rounded-lg text-sm italic mb-6">{{ question.explanation }}</p>
            <div class="flex gap-4">
              <button @click="resetQuiz" class="flex-1 border-2 py-3 rounded-xl font-bold text-gray-500">Try Again</button>
              <button @click="loadNextQuestion" class="flex-1 bg-green-600 text-white py-3 rounded-xl font-bold">Next ➔</button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="flex flex-col items-center justify-center h-64 bg-white rounded-3xl shadow-xl border border-gray-100">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-500 font-medium">Loading {{ selectedCert?.name }}...</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// --- Core State ---
const question = ref(null);
const selectedAnswers = ref([]);
const feedback = ref('');
const feedbackClass = ref('');
const showExplanation = ref(false);

// --- Exam & Session Logic ---
const sessionCount = ref(1);
const totalExamQuestions = ref(65); // Only one declaration, as a ref
const seenQuestionIds = ref([]);
const MAX_HISTORY = 30; 

// --- Performance Tracking ---
const showResults = ref(false);
const correctCount = ref(0);
const startTime = ref(null);
const totalExamTime = ref(0);
const categoryScores = ref({}); 

// --- Platform State ---
const currentView = ref('landing'); // 'landing' or 'quiz'
const selectedCert = ref(null);

// --- Timer Logic ---
const timer = ref(0);
const timerInterval = ref(null);

// --- Category Logic ---
const selectedCategory = ref('All');
const categories = ['All', 'Data Engineering', 'Algorithms', 'Modeling', 'Deployment', 'Security', 'AI Services'];

// --- Exam History ---
const examHistory = ref([]);

// --- Functions ---

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

const startTimer = () => {
  if (timerInterval.value) clearInterval(timerInterval.value);
  timer.value = 0; 
  timerInterval.value = setInterval(() => {
    timer.value++;
  }, 1000);
};

const fetchTotalCount = async () => {
  if (!selectedCert.value) return; // Guard clause
  try {
    const examCode = selectedCert.value.code;
    const cat = selectedCategory.value;
    const url = `http://localhost:8080/api/questions/count?examCode=${examCode}&category=${encodeURIComponent(cat)}`;
    
    const response = await fetch(url);
    const count = await response.json();
    totalExamQuestions.value = Math.min(count, 65);
  } catch (error) {
    console.error("Count Error:", error);
  }
};

const fetchQuestion = async () => {
  if (!selectedCert.value) return;
  try {
    const examCode = selectedCert.value.code;
    const cat = selectedCategory.value;
    const url = `http://localhost:8080/api/questions/random?examCode=${examCode}&category=${encodeURIComponent(cat)}`;

    const response = await fetch(url);
    const data = await response.json();
    
    if (seenQuestionIds.value.includes(data.id) && seenQuestionIds.value.length < MAX_HISTORY) {
        return fetchQuestion(); 
    }

    question.value = data;
    seenQuestionIds.value.push(data.id);
    if (!startTime.value) startTime.value = Date.now();
    startTimer();
  } catch (error) {
    console.error("API Error:", error);
    question.value = null; // Ensure we don't show an empty card
  }
};

const toggleAnswer = (optionId) => {
  if (showExplanation.value) return;
  if (question.value.multiSelect) {
    const index = selectedAnswers.value.indexOf(optionId);
    if (index > -1) selectedAnswers.value.splice(index, 1);
    else selectedAnswers.value.push(optionId);
  } else {
    selectedAnswers.value = [optionId];
  }
};

const checkAnswer = () => {
  if (!question.value) return;
  clearInterval(timerInterval.value);
  
  const correctIds = question.value.options
    .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
    .map(opt => String(opt.id));

  const selectedIds = selectedAnswers.value.map(id => String(id));
  const isCorrect = selectedIds.length === correctIds.length &&
                    selectedIds.every(id => correctIds.includes(id));

  if (isCorrect) correctCount.value++;
  
  const cat = question.value.category;
  if (!categoryScores.value[cat]) categoryScores.value[cat] = { correct: 0, total: 0 };
  categoryScores.value[cat].total++;
  if (isCorrect) categoryScores.value[cat].correct++;

  showExplanation.value = true;
  feedback.value = isCorrect ? "Correct!" : "Incorrect";
  feedbackClass.value = isCorrect 
    ? "bg-green-50 text-green-700 border-green-200" 
    : "bg-red-50 text-red-700 border-red-200";
};

const resetQuiz = () => {
  selectedAnswers.value = [];
  showExplanation.value = false;
  feedback.value = '';
};

const loadNextQuestion = async () => {
  if (sessionCount.value >= totalExamQuestions.value) {
    totalExamTime.value = Math.floor((Date.now() - startTime.value) / 1000);
    showResults.value = true;

    // SAVE TO DATABASE
    await fetch('http://localhost:8080/api/questions/history', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        examCode: selectedCert.value.code,
        scorePercentage: Math.round((correctCount.value / totalExamQuestions.value) * 100),
        correctCount: correctCount.value,
        totalQuestions: totalExamQuestions.value,
        timeSpentSeconds: totalExamTime.value,
        weakestCategory: getStudyPriorities()[0]?.name || 'N/A'
      })
    });
    
    return;
  }
  resetQuiz();
  sessionCount.value++;
  question.value = null; 
  await fetchQuestion();
};

const resetSession = async () => {
  seenQuestionIds.value = [];
  sessionCount.value = 1;
  correctCount.value = 0;
  startTime.value = null;
  categoryScores.value = {};
  showResults.value = false;
  
  await fetchTotalCount(); 
  resetQuiz();
  await fetchQuestion();
};

const getStudyPriorities = () => {
  const priorities = Object.entries(categoryScores.value).map(([cat, score]) => {
    return {
      name: cat,
      percentage: Math.round((score.correct / score.total) * 100),
      total: score.total
    };
  });
  return priorities.sort((a, b) => a.percentage - b.percentage).slice(0, 3);
};

const certifications = ref([
  { 
    code: 'MLS-C01', 
    name: 'Machine Learning Specialty', 
    icon: '🤖', 
    color: 'from-purple-500 to-indigo-600',
    desc: 'Master SageMaker, modeling, and AI engineering.'
  },
  { 
    code: 'AIF-C01', 
    name: 'AI Practitioner', 
    icon: '✨', 
    color: 'from-blue-400 to-cyan-500',
    desc: 'Foundational AI concepts and AWS AI services.'
  },
  { 
    code: 'SCS-C02', 
    name: 'Security Specialty', 
    icon: '🛡️', 
    color: 'from-red-500 to-orange-600',
    desc: 'Advanced security, encryption, and compliance.'
  },
  { 
    code: 'SAA-C03', 
    name: 'Solutions Architect', 
    icon: '🏗️', 
    color: 'from-green-500 to-teal-600',
    desc: 'Design resilient and scalable AWS architectures.'
  }
]);

// --- New Function to Start a Specific Exam ---
const selectCertification = async (cert) => {
  selectedCert.value = cert;
  // Update categories based on cert if needed, or keep 'All'
  selectedCategory.value = 'All'; 
  
  // Reuse your existing reset logic
  await resetSession(); 
  
  currentView.value = 'quiz';
};

const goBackToLanding = () => {
  currentView.value = 'landing';
  showResults.value = false;
};

const fetchHistory = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/questions/history');
    examHistory.value = await response.json();
  } catch (error) {
    console.error("Error fetching history:", error);
  }
};

// Call this when the app loads
onMounted(async () => {
  await fetchHistory();
  // ... existing fetch logic
});

onMounted(async () => {
  await fetchTotalCount();
  await fetchQuestion();
});
</script>