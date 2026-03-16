<template>
  <div class="min-h-screen bg-gray-50 py-12 px-4 font-sans">

    <div class="max-w-5xl mx-auto flex justify-between items-center mb-8 bg-white p-4 rounded-3xl shadow-sm border border-slate-100">
      <div class="flex items-center gap-2 px-4">
        <span class="text-2xl font-black text-blue-600 tracking-tighter italic">AWS.Hub</span>
      </div>

      <div class="px-4">
        <div v-if="currentUser" class="flex items-center gap-4 bg-slate-50 p-2 pr-4 rounded-2xl border border-slate-100">
          <div class="flex flex-col items-end">
            <span class="text-[10px] font-black uppercase tracking-widest text-slate-400">Cloud Ninja</span>
            <span class="text-sm font-bold text-slate-700">👋 {{ currentUser.fullName }}</span>
          </div>
          <div class="h-8 w-px bg-slate-200"></div>
          <button @click="logout" class="text-xs font-black text-red-500 hover:text-red-700 uppercase tracking-wider transition-colors">Logout</button>
        </div>
        <button
            v-else
            @click="showAuthModal = true"
            class="bg-blue-600 text-white px-8 py-2.5 rounded-2xl font-bold text-sm shadow-lg hover:bg-blue-700 transition-all"
        >
          Sign In
        </button>
      </div>
    </div>

    <div v-if="showAuthModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-[200] flex items-center justify-center p-4">
      <div class="bg-white rounded-[2rem] w-full max-w-sm overflow-hidden shadow-2xl relative p-10 text-center">

        <h2 class="text-2xl font-black text-slate-800 mb-2">
          {{ isSignup ? 'Join the Hub' : 'Welcome Back' }}
        </h2>
        <p class="text-slate-500 text-sm mb-8">
          {{ isSignup ? 'Create your account to save scores' : 'Enter your email to continue' }}
        </p>

        <div class="flex flex-col gap-3">
          <input
              v-model="authForm.email"
              type="email"
              placeholder="Your Email"
              class="w-full p-4 rounded-2xl border border-slate-200 outline-none focus:border-blue-500 transition-all text-center font-bold"
          />

          <input
              v-if="isSignup"
              v-model="authForm.fullName"
              type="text"
              placeholder="Full Name (e.g. Aaron Mosqueda)"
              class="w-full p-4 rounded-2xl border border-slate-200 outline-none focus:border-blue-500 transition-all text-center font-bold"
          />

          <button @click="handleAuth" class="w-full bg-blue-600 text-white p-4 rounded-2xl font-black shadow-lg hover:bg-blue-700 transition-all">
            {{ isSignup ? 'Create Account' : 'Log In' }}
          </button>

          <p class="mt-4 text-xs text-slate-500">
            {{ isSignup ? 'Already a member?' : 'New here?' }}
            <span @click="isSignup = !isSignup" class="text-blue-600 font-bold cursor-pointer hover:underline ml-1">
          {{ isSignup ? 'Log In' : 'Sign Up Now' }}
        </span>
          </p>
        </div>

        <button @click="showAuthModal = false" class="absolute top-4 right-6 text-slate-300 hover:text-slate-500 text-2xl">&times;</button>
      </div>
    </div>

    <div v-if="currentView === 'landing'" class="max-w-5xl mx-auto">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-extrabold text-gray-900 mb-4">AWS Certification Hub</h1>
        <p class="text-lg text-gray-600">Select your path and master the cloud.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
        <div v-for="cert in certifications" :key="cert.code" @click="selectCertification(cert)" class="group cursor-pointer bg-white rounded-3xl p-6 shadow-sm border border-gray-100 hover:shadow-xl hover:-translate-y-2 transition-all">
          <div :class="['w-12 h-12 rounded-xl flex items-center justify-center text-xl mb-4 text-white shadow-md', cert.color]">{{ cert.icon }}</div>
          <h3 class="font-bold text-gray-800">{{ cert.name }}</h3>
          <p class="text-gray-400 text-[10px] font-mono uppercase tracking-widest mt-1">{{ cert.code }}</p>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 bg-white rounded-3xl p-6 border border-gray-100 shadow-sm">
          <h2 class="text-xl font-bold mb-6">Recent Activity</h2>
          <div v-if="examHistory.length > 0">
            <div class="bg-white rounded-3xl overflow-hidden">
              <table class="w-full text-left border-collapse table-fixed">
                <thead class="bg-slate-50 border-b border-slate-100">
                <tr>
                  <th class="px-6 py-4 text-[10px] font-black uppercase tracking-widest text-slate-400 w-[30%]">Exam</th>
                  <th class="px-6 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400 w-[15%]">Review</th>
                  <th class="px-6 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400 w-[15%]">Score</th>
                  <th class="px-6 py-4 text-right text-[10px] font-black uppercase tracking-widest text-slate-400 w-[25%]">Area</th>
                  <th class="px-6 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400 w-[15%]"></th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-50">
                <tr v-for="attempt in examHistory" :key="attempt.id" class="hover:bg-slate-50/50 transition-colors">
                  <td class="px-6 py-4">
                    <div class="font-bold text-slate-700 leading-tight">{{ attempt.examCode }}</div>
                    <div class="text-[10px] text-slate-400 mt-0.5">{{ formatDate(attempt.completedAt) }}</div>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <button @click="openReviewRoom(attempt.id)" class="text-lg hover:scale-125 transition-transform">👁️</button>
                  </td>
                  <td class="px-6 py-4 text-center">
          <span :class="['px-2.5 py-1 rounded-lg font-black text-xs border', attempt.scorePercentage >= 75 ? 'bg-green-50 text-green-600 border-green-100' : 'bg-red-50 text-red-600 border-red-100']">
            {{ attempt.scorePercentage }}%
          </span>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <span class="bg-amber-50 text-amber-700 text-[9px] font-black uppercase px-2 py-1 rounded-md border border-amber-100">{{ attempt.weakestCategory }}</span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <button @click="deleteAttempt(attempt.id)" class="opacity-20 hover:opacity-100 hover:text-red-500 transition-all text-xs">🗑️</button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-else class="text-center py-10 text-gray-400">Complete an exam to see your stats.</div>
        </div>
        <div class="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm">
          <h2 class="text-xl font-bold mb-6">🏆 Hall of Fame</h2>
          <div class="space-y-3">
            <div v-for="(entry, index) in leaderboard" :key="index"
                 class="flex items-center justify-between p-3 rounded-2xl bg-slate-50 border border-slate-100">
              <div class="flex items-center gap-3">
      <span class="text-sm">
        {{ index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : index + 1 }}
      </span>

                <div class="flex flex-col">
        <span class="font-bold text-slate-700 text-sm truncate max-w-[120px]">
          {{ entry.fullName || 'Anonymous Ninja' }}
        </span>
                  <span class="text-[9px] font-black text-blue-500 uppercase tracking-widest">
          {{ entry.examCode || 'AWS EXAM' }}
        </span>
                </div>
              </div>

              <span class="bg-blue-600 text-white px-2.5 py-1 rounded-xl font-black text-[10px] shadow-sm shadow-blue-100">
      {{ entry.scorePercentage }}%
    </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentView === 'quiz'" class="max-w-3xl mx-auto relative">

      <div v-if="showResults" class="bg-white rounded-[2rem] shadow-2xl p-10 text-center border border-gray-100">
        <h1 class="text-3xl font-black text-slate-800 mb-8 text-center">Exam Results</h1>
        <div class="grid grid-cols-2 gap-4 mb-8 text-left">
          <div class="bg-blue-50 p-6 rounded-2xl text-center">
            <p class="text-blue-600 text-[10px] font-black uppercase tracking-widest mb-1">Score</p>
            <p class="text-4xl font-black text-blue-900">{{ Math.round((correctCount / sessionCount) * 100) }}%</p>
          </div>
          <div class="bg-amber-50 p-6 rounded-2xl text-center">
            <p class="text-amber-600 text-[10px] font-black uppercase tracking-widest mb-1">Time</p>
            <p class="text-2xl font-black text-amber-900">{{ formatTime(timer) }}</p>
          </div>
        </div>
        <div class="flex flex-col gap-3">
          <button @click="resetSession" class="bg-blue-600 text-white py-4 rounded-2xl font-black shadow-lg">Start New Session</button>
          <button @click="goBackToLanding" class="bg-slate-100 text-slate-600 py-4 rounded-2xl font-bold">Return to Dashboard</button>
        </div>
      </div>

      <div v-else-if="question" class="bg-white rounded-[2rem] shadow-xl overflow-hidden border border-slate-200 relative">

        <div v-if="isPaused" class="absolute inset-0 bg-white/95 backdrop-blur-md z-50 flex flex-col items-center justify-center text-center p-8">
          <div class="w-16 h-16 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center text-3xl mb-4">⏸️</div>
          <h2 class="text-2xl font-black text-slate-800">Exam Paused</h2>
          <button @click="togglePause" class="mt-6 bg-slate-800 text-white px-8 py-3 rounded-2xl font-bold">Resume Exam</button>
        </div>

        <div class="bg-blue-600 p-6 flex justify-between items-center text-white">
          <div>
            <p class="text-[10px] font-black uppercase tracking-widest opacity-70">{{ question.category }}</p>
            <h2 class="text-lg font-bold">Question {{ sessionCount }} of {{ totalExamQuestions }}</h2>
          </div>
          <div class="flex items-center gap-4">
            <span class="font-mono font-bold bg-blue-500/50 px-3 py-1 rounded-lg">{{ formatTime(timer) }}</span>
            <button @click="togglePause" class="bg-white text-blue-600 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg">
              {{ isPaused ? 'Resume' : 'Pause' }}
            </button>
          </div>
        </div>

        <div class="bg-slate-50 px-6 py-3 border-b flex justify-between items-center">
          <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest">Practice Mode</span>
          <button @click="confirmEndEarly" class="text-[10px] font-black text-red-500 hover:text-red-700 uppercase tracking-widest">End & Grade</button>
        </div>

        <div class="p-8">
          <p class="text-xl font-bold text-slate-800 mb-8">{{ question.questionText }}</p>
          <div class="space-y-3">
            <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)" :disabled="showExplanation"
                    :class="['w-full text-left p-4 rounded-2xl border-2 transition-all font-medium',
                    selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-slate-100 bg-slate-50 text-slate-600']">
              {{ opt.optionText }}
            </button>
          </div>
          <button v-if="!showExplanation" @click="checkAnswer" :disabled="selectedAnswers.length === 0" class="w-full mt-8 bg-blue-600 text-white py-4 rounded-2xl font-black shadow-xl">Submit Answer</button>

          <div v-if="showExplanation" class="mt-8 animate-in fade-in duration-500">
            <div :class="['p-4 rounded-2xl text-center font-black uppercase mb-4', feedbackClass]">{{ feedback }}</div>
            <p class="bg-amber-50 p-5 rounded-2xl text-sm italic border border-amber-100 text-slate-700">{{ question.explanation }}</p>
            <button @click="loadNextQuestion" class="w-full mt-4 bg-green-600 text-white py-4 rounded-2xl font-black shadow-lg">Next Question ➔</button>
          </div>
        </div>
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
const totalExamQuestions = ref(65);
const seenQuestionIds = ref([]);
const allQuestionsInSession = ref([]); // Added this to prevent "undefined" errors
const selectedAnswersRecord = ref({}); // Added this to prevent "undefined" errors
const MAX_HISTORY = 30;

// --- Performance Tracking ---
const showResults = ref(false);
const correctCount = ref(0);
const startTime = ref(null);
const totalExamTime = ref(0);
const categoryScores = ref({});

// --- Platform State ---
const currentView = ref('landing');
const selectedCert = ref(null);

// --- Timer Logic ---
const timer = ref(0);
let timerInterval = null;

// --- Category Logic ---
const selectedCategory = ref('All');
const categories = ['All', 'Data Engineering', 'Algorithms', 'Modeling', 'Deployment', 'Security', 'AI Services'];

// --- Exam History ---
const examHistory = ref([]);

// --- User Login ---
const currentUser = ref(null);
const showAuthModal = ref(false);
const isSignup = ref(false);
const authForm = ref({ email: '', password: '', fullName: '' });

// --- Review & Print ---
const showReviewModal = ref(false);
const reviewQuestions = ref([]);

const printReview = () => {
  window.print();
};

const openReviewRoom = async (attemptId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/questions/review/${attemptId}`);
    const data = await response.json();
    reviewQuestions.value = data;
    showReviewModal.value = true;
  } catch (error) {
    console.error("Error loading review room:", error);
  }
};

const resetSession = async () => {
  // 1. Ensure we still have a certification selected
  if (!selectedCert.value) {
    console.error("No certification selected. Returning to landing.");
    currentView.value = 'landing';
    return;
  }

  // 2. Reset counts and tracking
  sessionCount.value = 1;
  correctCount.value = 0;
  timer.value = 0; // Reset the clock

  // 3. Clear session-specific arrays
  seenQuestionIds.value = [];
  allQuestionsInSession.value = [];
  selectedAnswersRecord.value = {};
  selectedAnswers.value = [];
  categoryScores.value = {};

  // 4. UI State - Switch back to quiz view
  showResults.value = false;
  showExplanation.value = false;
  currentView.value = 'quiz'; // Ensure we are in quiz mode
  question.value = null;      // Clear the old question to trigger loader

  // 5. Restart logic
  startTimer();

  try {
    await fetchQuestion();
  } catch (error) {
    console.error("Failed to restart session:", error);
    currentView.value = 'landing';
  }
};

// --- Helper Functions ---
const formatDate = (dateString) => {
  if (!dateString) return 'No Date';

  // This converts the Java LocalDateTime string into a readable format
  const date = new Date(dateString);

  return date.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
};

const formatTime = (seconds) => {
  if (!seconds && seconds !== 0) return '00:00';

  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;

  // padStart ensures we always have two digits (e.g., 05 instead of 5)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

const goBackToLanding = () => {
  // 1. Stop any active timers
  if (timerInterval) clearInterval(timerInterval);

  // 2. Reset the quiz states
  showResults.value = false;
  showExplanation.value = false;
  question.value = null;

  // 3. Switch the view back to the dashboard
  currentView.value = 'landing';

  // 4. Refresh the history so the new attempt shows up
  fetchHistory();
};

const isCorrect = (q, userPick) => {
  const correctIds = q.options
      .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
      .map(opt => String(opt.id));
  const pickedIds = Array.isArray(userPick) ? userPick.map(id => String(id)) : [String(userPick)];
  return pickedIds.length === correctIds.length && pickedIds.every(id => correctIds.includes(id));
};

const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval); // Clear any existing ones first
  timerInterval = setInterval(() => {
    timer.value++;
  }, 1000);
};

const fetchTotalCount = async () => {
  if (!selectedCert.value) return;
  try {
    const examCode = selectedCert.value.code;
    const cat = selectedCategory.value;
    const url = `http://localhost:8080/api/questions/count?examCode=${examCode}&category=${encodeURIComponent(cat)}&t=${Date.now()}`;
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
    const uid = currentUser.value?.id || '';

    const url = `http://localhost:8080/api/questions/random?examCode=${examCode}&category=${encodeURIComponent(cat)}&userId=${uid}&t=${Date.now()}`;
    const response = await fetch(url);
    const data = await response.json();

    // --- CHECK HERE ---
    // If you see "sessionCount.value++" anywhere in this function, DELETE IT.

    question.value = data;

    // Track this question for the Review Room later
    if (!allQuestionsInSession.value.find(q => q.id === data.id)) {
      allQuestionsInSession.value.push(data);
    }
  } catch (error) {
    console.error("API Error:", error);
  }
};

const loadNextQuestion = async () => {
  // 1. Check if we just finished the last question
  if (sessionCount.value >= totalExamQuestions.value) {
    // Stop the quiz and show the final score
    finishExam();
    return;
  }

  // 2. If not finished, move to the next number
  sessionCount.value++;

  // 3. Reset UI and fetch next question
  showExplanation.value = false;
  selectedAnswers.value = [];
  await fetchQuestion();

  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const checkAnswer = () => {
  if (!question.value) return;
  // Save the answer record for history logic
  selectedAnswersRecord.value[question.value.id] = [...selectedAnswers.value];

  const correctIds = question.value.options
      .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
      .map(opt => String(opt.id));

  const selectedIds = selectedAnswers.value.map(id => String(id));
  const correct = selectedIds.length === correctIds.length &&
      selectedIds.every(id => correctIds.includes(id));

  if (correct) correctCount.value++;

  const cat = question.value.category;
  if (!categoryScores.value[cat]) categoryScores.value[cat] = { correct: 0, total: 0 };
  categoryScores.value[cat].total++;
  if (correct) categoryScores.value[cat].correct++;

  showExplanation.value = true;
  feedback.value = correct ? "Correct!" : "Incorrect";
  feedbackClass.value = correct
      ? "bg-green-50 text-green-700 border-green-200"
      : "bg-red-50 text-red-700 border-red-200";
};

const saveResults = async () => {

  const missedIds = allQuestionsInSession.value
      .filter(q => {
        const userPick = selectedAnswersRecord.value[q.id];
        if (!userPick || (Array.isArray(userPick) && userPick.length === 0)) return true;
        return !isCorrect(q, userPick);
      })
      .map(q => q.id)
      .join(",");

  const historyData = {
    userId: currentUser.value?.id,
    examCode: selectedCert.value?.code,
    scorePercentage: Math.round((correctCount.value / totalExamQuestions.value) * 100),
    weakestCategory: getStudyPriorities()[0]?.name || 'All Good',
    missedQuestionIds: missedIds
  };
  console.log("Sending to Backend:", historyData);

  try {
    const response = await fetch('http://localhost:8080/api/questions/history/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(historyData)
    });

    if (response.ok) {
      // 1. Refresh the table data in the background
      await fetchHistory();

      // 2. STAY ON THE RESULTS PAGE
      // We removed "currentView.value = 'landing'" so the screen doesn't switch.
      showResults.value = true;

      console.log("Exam results saved and history refreshed.");
    }
  } catch (error) {
    console.error("Error saving exam history:", error);
  }
};

const fetchHistory = async () => {
  // 1. Get the ID from your reactive currentUser ref
  const userId = currentUser.value?.id;

  // 2. If no user is logged in, clear the table and stop
  if (!userId) {
    examHistory.value = [];
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/api/questions/history?userId=${userId}`);
    if (response.ok) {
      const data = await response.json();
      examHistory.value = data;
    }
  } catch (error) {
  }
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

// 1. Add the Certifications list
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
  },
  {
    code: 'DEA-C01',
    name: 'Data Engineering',
    icon: '🏗️',
    color: 'from-orange-500 to-yellow-600',
    desc: 'Master data ingestion, orchestration with Airflow, and Redshift data modeling.'
  }
]);

// 2. Add the Leaderboard ref back
const leaderboard = ref([]);

// --- 2. The Selection Logic ---
const selectCertification = async (cert) => {
  selectedCert.value = cert;
  currentView.value = 'quiz';
  selectedCategory.value = 'All';

  // 1. Get the total count for the selected exam
  await fetchTotalCount();

  // 2. Start the session inside the try block
  try {
    await resetSession(); // ONLY CALL THIS ONCE
  } catch (error) {
    console.error("Failed to load first question:", error);
    // If it fails, send them back to the landing page
    currentView.value = 'landing';
  }
};

const fetchLeaderboard = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/questions/leaderboard');
    if (response.ok) {
      const data = await response.json();
      console.log("Leaderboard Data:", data); // Check your console for this!
      leaderboard.value = data;
    }
  } catch (error) {
    console.error("Leaderboard fetch error:", error);
  }
};

// --- 4. The Auth Logic (Login/Signup) ---
const handleAuth = async () => {
  const endpoint = isSignup.value ? 'signup' : 'login';

  try {
    const response = await fetch(`http://localhost:8080/api/auth/${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(authForm.value)
    });

    if (response.ok) {
      if (isSignup.value) {
        // TWEAK 1: Success message for Signup
        isSignup.value = false;
        alert("Account created! Please log in with your email."); // Changed 'nickname' to 'email'
      } else {
        const user = await response.json();
        currentUser.value = user;
        localStorage.setItem('aws_user', JSON.stringify(user));
        showAuthModal.value = false;

        // TWEAK 2: Reset the form so it's clean for next time
        authForm.value = { email: '', fullName: '' };

        await fetchHistory();
        await fetchLeaderboard();
      }
    } else {
      const errorData = await response.text();
      alert(`Auth failed: ${errorData || 'Check your credentials.'}`);
    }
  } catch (error) {
    alert("Could not connect to the server. Is your Spring Boot backend running?");
  }
};

const toggleAnswer = (option) => {
  // If we are showing the explanation (meaning the question is over), disable clicking
  if (showExplanation.value) return;

  const maxChoices = question.value?.correctAnswerCount || 1;

  if (maxChoices === 1) {
    // Single Choice: Just replace the current selection
    selectedAnswers.value = [option];
  } else {
    // Multiple Response: Add/Remove from the list
    const index = selectedAnswers.value.indexOf(option);
    if (index > -1) {
      // If already selected, remove it
      selectedAnswers.value.splice(index, 1);
    } else if (selectedAnswers.value.length < maxChoices) {
      // If not selected and we haven't hit the limit, add it
      selectedAnswers.value.push(option);
    }
  }
};

const deleteAttempt = async (attemptId) => {
  if (!confirm("Are you sure you want to remove this attempt from your history?")) return;

  try {
    const response = await fetch(`http://localhost:8080/api/questions/history/${attemptId}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      // Refresh the table locally so the row disappears immediately
      examHistory.value = examHistory.value.filter(a => a.id !== attemptId);
      console.log("Attempt deleted successfully");
    } else {
      alert("Failed to delete. Check if the backend endpoint exists.");
    }
  } catch (error) {
    console.error("Delete Error:", error);
  }
};

const logout = () => {
  // 1. Remove the user data from the browser's memory
  localStorage.removeItem('aws_user');

  // 2. Clear the reactive state
  currentUser.value = null;

  // 3. Optional: Redirect to landing and clear history
  currentView.value = 'landing';
  examHistory.value = [];

  // 4. Refresh the leaderboard so it shows guest-level data if needed
  fetchLeaderboard();

  console.log("User logged out successfully.");
};

const finishExam = async () => {
  // 1. Stop the clock
  if (timerInterval) clearInterval(timerInterval);

  // 2. Switch to results view
  currentView.value = 'quiz'; // We stay in the quiz component container
  showResults.value = true;   // But we trigger the Results overlay

  // 3. Save the data to the database
  await saveResults();

  // 4. Scroll to top so they see the score
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const confirmEndEarly = () => {
  if (confirm("End and grade now?")) {
    // FIX: Instead of changing the global totalExamQuestions,
    // we just tell the app how many questions were actually attempted.
    const questionsAttempted = sessionCount.value;

    // Force the score calculation to be correct for THIS session
    correctCount.value = Math.min(correctCount.value, questionsAttempted);

    // Trigger finish
    finishExam();
  }
};

const isPaused = ref(false);

const togglePause = () => {
  isPaused.value = !isPaused.value;

  if (isPaused.value) {
    // Stop the clock
    if (timerInterval) clearInterval(timerInterval);
  } else {
    // Restart the clock
    startTimer();
  }
};

const debugFinish = () => {
  // Simulate finishing 65 questions
  sessionCount.value = totalExamQuestions.value;
  correctCount.value = 48; // Give yourself a passing score (approx 74%)

  // Fill the "missed questions" list so the Priorities logic works
  allQuestionsInSession.value = [question.value];

  finishExam();
};

onMounted(() => {
  // Check for saved user session
  const savedUser = localStorage.getItem('aws_user');
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser);
  }

  // Refresh all data
  fetchHistory();
  fetchLeaderboard(); // <--- This MUST be here!
});
</script>

<style scoped>
@media print {
  body * { visibility: hidden; }
  .fixed.inset-0, .fixed.inset-0 * { visibility: visible; }
  .fixed.inset-0 {
    position: absolute !important;
    left: 0 !important; top: 0 !important;
    width: 100% !important; height: auto !important;
    background: white !important; display: block !important;
  }
  button, .p-4.bg-white.border-t { display: none !important; }
  .overflow-y-auto { overflow: visible !important; max-height: none !important; }
  .shadow-2xl, .rounded-3xl { box-shadow: none !important; border: none !important; }
}
</style>