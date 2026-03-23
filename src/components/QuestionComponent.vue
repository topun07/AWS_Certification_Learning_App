<template>
  <div class="min-h-screen bg-gray-50 pb-12 px-4 font-sans relative">

    <!-- Top Navigation Bar -->
    <div class="max-w-5xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4 mb-6 bg-white p-6 md:p-4 rounded-3xl shadow-sm border border-slate-100 mt-6">
      <div class="flex items-center gap-2 px-4">
        <span class="text-3xl md:text-2xl font-black text-blue-600 tracking-tighter italic">AWS.Hub</span>
      </div>

      <div v-if="currentUser" class="flex items-center gap-6 bg-white/80 backdrop-blur-md p-3 px-6 rounded-[1.5rem] border border-slate-200 shadow-xl transition-all hover:shadow-blue-100/50">
        <div class="flex flex-col items-end">
          <span class="text-[10px] font-black uppercase tracking-[0.25em] text-blue-600 mb-1">Active Session: ARCHITECT</span>
          <span class="text-2xl font-black text-slate-900 tracking-tighter leading-none drop-shadow-sm">{{ currentUser.username }}</span>
        </div>
        <div class="h-10 w-px bg-slate-200"></div>
        <button @click="logout" class="group relative flex flex-col items-center">
          <span class="text-[10px] font-black text-red-500 uppercase tracking-widest transition-all group-hover:text-red-700">Logout</span>
          <span class="h-0.5 w-0 bg-red-500 transition-all group-hover:w-full"></span>
        </button>
      </div>

      <div v-else class="flex items-center gap-4 px-4">
        <button
            @click="showAuthModal = true; isSignup = false"
            class="text-sm font-bold text-slate-500 hover:text-blue-600 transition-colors"
        >
          Log In
        </button>

        <button
            @click="showAuthModal = true; isSignup = true"
            class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-black px-6 py-2.5 rounded-xl shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5"
        >
          Sign Up
        </button>
      </div>
    </div>

    <!-- View: Landing Page -->
    <div v-if="currentView === 'landing'" class="max-w-7xl mx-auto px-4 md:px-8 lg:px-12">
      <div class="text-center mb-12 md:mb-20 mt-8 md:mt-12">
        <h1 class="text-4xl md:text-6xl lg:text-7xl font-extrabold text-gray-900 mb-4 md:mb-6 tracking-tight">AWS Certification Hub</h1>
        <p class="text-lg md:text-2xl lg:text-3xl text-gray-600 font-medium">Select your path and master the cloud.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
        <div v-for="cert in certifications" :key="cert.code" @click="selectCertification(cert)" class="group cursor-pointer bg-white rounded-3xl p-6 shadow-sm border border-gray-100 hover:shadow-xl hover:-translate-y-2 transition-all">
          <div class="h-16 w-16 md:h-20 md:w-20 mb-4 flex-shrink-0">
            <img v-if="cert.image" :src="cert.image" :alt="cert.code" class="w-full h-full object-contain drop-shadow-md group-hover:scale-110 transition-transform duration-300" />
            <div v-else :class="['w-full h-full rounded-xl flex items-center justify-center text-2xl text-white shadow-md', cert.color]">
              {{ cert.icon }}
            </div>
          </div>
          <h3 class="font-bold text-gray-800 md:text-xl">{{ cert.name }}</h3>
          <p class="text-gray-400 text-[10px] md:text-xs font-mono uppercase tracking-widest mt-1 md:mt-2">{{ cert.code }}</p>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 bg-white rounded-3xl p-6 border border-gray-100 shadow-sm">
          <h2 class="text-2xl font-bold mb-6">Recent Activity</h2>
          <div v-if="examHistory.length > 0">
            <div class="overflow-x-auto -mx-6">
              <div class="inline-block min-w-full align-middle px-6">
                <table class="min-w-[600px] w-full text-left border-collapse">
                  <thead class="bg-slate-50 border-b border-slate-100">
                  <tr>
                    <th class="px-6 py-4 text-xs font-black uppercase tracking-widest text-slate-400 w-[30%]">Exam</th>
                    <th class="px-6 py-4 text-center text-xs font-black uppercase tracking-widest text-slate-400 w-[15%]">Review</th>
                    <th class="px-6 py-4 text-center text-xs font-black uppercase tracking-widest text-slate-400 w-[15%]">Score</th>
                    <th class="px-6 py-4 text-right text-xs font-black uppercase tracking-widest text-slate-400 w-[25%]">Area</th>
                    <th class="px-6 py-4 w-[15%]"></th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-50">
                  <tr v-for="attempt in examHistory" :key="attempt.id" class="hover:bg-slate-50/50 transition-colors">
                    <td class="px-6 py-4">
                      <div class="text-base font-bold text-slate-700 leading-tight">{{ attempt.examCode }}</div>
                      <div class="text-xs text-slate-400 mt-0.5">{{ formatDate(attempt.completedAt) }}</div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <button @click="openReviewRoom(attempt.id)" class="text-xl">👁️</button>
                    </td>
                    <td class="px-6 py-4 text-center">
                        <span :class="['px-3 py-1 rounded-lg font-black text-sm border', attempt.scorePercentage >= 75 ? 'bg-green-50 text-green-600 border-green-100' : 'bg-red-50 text-red-600 border-red-100']">
                          {{ attempt.scorePercentage }}%
                        </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                    <span class="bg-amber-50 text-amber-700 text-[10px] font-black uppercase px-2 py-1 rounded-md border border-amber-100">
                      {{ attempt.weakestCategory || 'MIXED' }}
                    </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <button
                          @click="deleteAttempt(attempt.id)"
                          class="text-slate-400 hover:text-red-600 hover:scale-125 transition-all text-lg"
                          title="Delete this attempt"
                      >
                        🗑️
                      </button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-10 text-gray-400">Complete an exam to see your stats.</div>
        </div>
        <div class="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm flex flex-col h-full">
          <h2 class="text-2xl font-bold mb-6">✨ Ascended Masters</h2>

          <div v-if="hallOfFame.length > 0" class="flex flex-col gap-3 flex-grow">
            <div v-for="(record, index) in hallOfFame" :key="index" class="flex items-center justify-between p-4 rounded-2xl bg-slate-50 border border-slate-100 hover:border-amber-200 hover:shadow-md transition-all group">

              <div class="flex items-center gap-4">
                <div :class="[
          'w-10 h-10 rounded-full flex items-center justify-center font-black text-lg shadow-sm',
          index === 0 ? 'bg-amber-400 text-white shadow-amber-200' :
          index === 1 ? 'bg-slate-300 text-slate-700' :
          index === 2 ? 'bg-orange-300 text-orange-800' : 'bg-blue-50 text-blue-600'
        ]">
                  #{{ index + 1 }}
                </div>

                <div>
                  <p class="font-black text-slate-700 group-hover:text-blue-600 transition-colors">{{ record.examCode }}</p>
                  <p class="text-xs text-slate-400 font-mono tracking-widest uppercase mt-0.5">Operative ID: {{ record.userId }}</p>
                </div>
              </div>

              <div class="text-2xl font-black text-slate-800">
                {{ record.scorePercentage }}<span class="text-sm text-slate-400">%</span>
              </div>

            </div>
          </div>

          <div v-else class="flex flex-col items-center justify-center py-12 text-slate-400 flex-grow border-2 border-dashed border-slate-100 rounded-2xl">
            <span class="text-4xl mb-3 opacity-50">☁️</span>
            <p class="font-bold">No legends yet.</p>
            <p class="text-sm">Score 100% to claim the top spot!</p>
          </div>
        </div>
      </div>
    </div>

    <!-- View: Quiz / Results -->
    <div v-else-if="currentView === 'quiz'" class="max-w-5xl mx-auto px-4 md:px-8 lg:px-12">

      <!-- Results Screen -->
      <div v-if="showResults" class="bg-white rounded-[2rem] shadow-2xl p-10 text-center border border-gray-100">
        <div class="mb-10 text-center flex flex-col items-center">
          <div class="mb-8 flex justify-center h-32 md:h-40">
            <img
                v-if="examResultMessage.badge"
                :src="examResultMessage.badge"
                class="h-full object-contain drop-shadow-[0_15px_25px_rgba(16,185,129,0.3)] animate-[bounce_3s_infinite]"
                alt="Certification Badge"
            />
            <div v-else class="text-7xl md:text-8xl animate-bounce">{{ examResultMessage.icon }}</div>
          </div>

          <h1 :class="['text-3xl md:text-5xl font-black mb-4 tracking-tight', examResultMessage.color]">
            {{ examResultMessage.title }}
          </h1>

          <div class="bg-slate-50 border border-slate-100 p-6 rounded-2xl max-w-2xl mx-auto shadow-inner">
            <p class="text-slate-600 font-mono text-sm md:text-base leading-relaxed italic">
              "{{ examResultMessage.text }}"
            </p>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 mb-8 text-left">
          <div class="bg-blue-50 p-6 rounded-2xl text-center">
            <p class="text-blue-600 text-[10px] font-black uppercase tracking-widest mb-1">Score</p>
            <p class="text-4xl md:text-5xl font-black text-blue-900">{{ finalScore }}%</p>
          </div>
          <div class="bg-amber-50 p-6 rounded-2xl text-center">
            <p class="text-amber-600 text-[10px] font-black uppercase tracking-widest mb-1">Time</p>
            <p class="text-2xl font-black text-amber-900">{{ formattedTime }}</p>
          </div>
        </div>

        <div class="flex flex-col gap-3">

          <button
              @click="openReviewRoom(lastAttemptId)"
              class="bg-orange-500 hover:bg-amber-500 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
          >
            🔍 Review My Answers
          </button>

          <button
              @click="handlePrint"
              class="bg-slate-800 hover:bg-slate-900 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
          >
            📄 Save Results as PDF
          </button>

          <button
              @click="resetSession"
              class="bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-indigo-600 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:scale-[1.02] transition-all duration-300"
          >
            Start New Session
          </button>

          <button
              @click="goBackToLanding"
              class="bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-800 py-4 rounded-2xl font-bold transition-colors duration-300 border border-slate-200"
          >
            Return to Dashboard
          </button>

        </div>
      </div>

      <!-- Active Quiz Screen -->
      <div v-else-if="question" class="bg-white rounded-[2rem] shadow-xl overflow-hidden border border-slate-200 relative">
        <div v-if="isPaused" class="absolute inset-0 bg-white/95 backdrop-blur-md z-50 flex flex-col items-center justify-center text-center p-8">
          <div class="w-16 h-16 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center text-3xl mb-4">⏸️</div>
          <h2 class="text-2xl font-black text-slate-800">Exam Paused</h2>
          <button @click="togglePause" class="mt-6 bg-slate-800 text-white px-8 py-3 rounded-2xl font-bold">Resume Exam</button>
        </div>

        <div class="bg-blue-600 p-6 flex justify-between items-center text-white">
          <div>
            <p class="text-[10px] font-black uppercase tracking-widest opacity-70">{{ question.category }}</p>
            <h2 class="text-lg font-bold">Question {{ Math.max(allQuestionsInSession.length, 1) }} of {{ totalExamQuestions }}</h2>
          </div>
          <div class="flex items-center gap-4">
            <span class="font-mono font-bold bg-blue-500/50 px-3 py-1 rounded-lg">{{ formattedTime }}</span>
            <button @click="togglePause" class="bg-white text-blue-600 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg">{{ isPaused ? 'Resume' : 'Pause' }}</button>
            <button @click="forceGradeExam" class="bg-white/20 hover:bg-white/30 text-white px-3 py-1 rounded-lg text-[10px] font-black uppercase tracking-widest transition-all border border-white/30">
              Grade Now 🏁
            </button>
          </div>
        </div>

        <div class="p-8">
          <p class="text-xl md:text-3xl lg:text-3xl font-extrabold text-slate-800 mb-8 md:mb-12 leading-relaxed md:leading-tight">
            {{ question.questionText }}
          </p>
          <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)"
                  :class="['w-full text-left p-4 md:p-6 lg:p-8 rounded-2xl md:rounded-[2rem] border-2 transition-all flex items-center gap-4 md:gap-6', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50' : 'border-slate-100 bg-slate-50 hover:border-blue-200 hover:shadow-md']">

            <div :class="['w-6 h-6 md:w-8 md:h-8 border-2 md:border-4 flex-none flex items-center justify-center transition-all', (question.options.filter(o => o.isCorrect).length > 1) ? 'rounded-md' : 'rounded-full', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-500' : 'border-slate-300']">
              <div v-if="selectedAnswers.includes(opt.id)" class="w-2 h-2 md:w-3 md:h-3 bg-white rounded-full"></div>
            </div>

            <span class="font-medium md:font-bold text-slate-700 md:text-xl lg:text-2xl">{{ opt.optionText }}</span>
          </button>

          <button
              v-if="!showExplanation"
              @click="checkAnswer"
              :disabled="selectedAnswers.length === 0"
              class="w-full mt-8 py-4 rounded-2xl font-black text-white bg-blue-600
         transition-all duration-300 ease-in-out
         hover:-translate-y-1 hover:shadow-[0_10px_40px_-10px_rgba(37,99,235,0.6)]
         active:translate-y-1 active:shadow-sm
         disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
          >
            Submit Answer
          </button>

          <div v-if="showExplanation" class="mt-8">
            <div :class="['p-4 rounded-2xl text-center font-black uppercase mb-4', feedbackClass]">{{ feedback }}</div>
            <p class="bg-amber-50 p-5 rounded-2xl text-sm italic border border-amber-100 text-slate-700">{{ question.explanation }}</p>
            <button @click="loadNextQuestion" class="w-full mt-4 bg-green-600 text-white py-4 rounded-2xl font-black shadow-lg">Next Question ➔</button>
            <button @click="forceGradeExam" class="w-full mt-6 bg-slate-100 text-slate-500 py-3 rounded-2xl font-bold border-2 border-dashed border-slate-200 hover:bg-slate-200 hover:text-slate-700 transition-all">
              🏁 Grade Now
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <div v-if="showAuthModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-[200] flex items-center justify-center p-4">
      <div class="bg-white rounded-[2rem] w-full max-w-sm overflow-hidden shadow-2xl relative p-10 text-center">

        <h2 class="text-2xl font-black text-slate-800 mb-6">
          {{ isSignup ? 'Join the Hub' : 'Welcome Back' }}
        </h2>

        <div class="flex flex-col gap-3">
          <input v-model="authForm.email" type="email" placeholder="Email Address" class="w-full p-4 rounded-2xl border border-slate-200 text-center font-bold focus:border-blue-500 outline-none auth-input" />

          <template v-if="isSignup">
            <input v-model="authForm.username" type="text" placeholder="Choose a Codename" class="w-full p-4 rounded-2xl border border-slate-200 text-center font-bold focus:border-blue-500 outline-none auth-input" />
            <input v-model="authForm.fullName" type="text" placeholder="Your Legendary Name" class="w-full p-4 rounded-2xl border border-slate-200 text-center font-bold focus:border-blue-500 outline-none auth-input" />
          </template>

          <input v-model="authForm.passwordhash" type="password" placeholder="Secret Access: Password" class="w-full p-4 rounded-2xl border border-slate-200 text-center font-bold focus:border-blue-500 outline-none auth-input" />

          <div v-if="authError" class="bg-red-900/10 border border-red-500/50 rounded-xl p-3 mt-1 animate-pulse text-center">
            <p class="text-[10px] font-mono font-black text-red-600 uppercase tracking-widest flex items-center justify-center gap-2">
              <span>⚠️</span>
              <span>{{ authError }}</span>
            </p>
          </div>

          <button @click="handleAuth" class="btn-primary-glow w-full bg-blue-600 text-white p-4 rounded-2xl font-black shadow-lg hover:bg-blue-700 transition-all mt-2">
            {{ isSignup ? 'Create Account' : 'Log In' }}
          </button>

          <p class="text-xs mt-4 font-bold text-slate-400">
            {{ isSignup ? 'Already a member?' : 'New to the network?' }}
            <span @click="isSignup = !isSignup" class="text-blue-600 cursor-pointer hover:underline ml-1">
              {{ isSignup ? 'Log In here' : 'Create an Account' }}
            </span>
          </p>
        </div>

        <button @click="showAuthModal = false" class="absolute top-4 right-6 text-slate-300 hover:text-slate-500 text-2xl transition-colors">&times;</button>
      </div>
    </div>

    <div v-if="showGuestLimitModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-md z-[400] flex items-center justify-center p-4">
      <div class="bg-slate-900 border border-cyan-500/50 rounded-[2rem] w-full max-w-md overflow-hidden shadow-[0_0_30px_rgba(6,182,212,0.3)] relative p-10 text-center">

        <div class="text-5xl mb-4 animate-pulse">🛑</div>

        <h2 class="text-2xl font-black text-cyan-400 mb-4 font-mono uppercase tracking-widest">
          Free Compute Exhausted
        </h2>

        <p class="text-slate-300 mb-8 font-mono text-sm leading-relaxed italic">
          "You've taken your first step into a larger world..."<br><br>
          <span class="not-italic text-slate-400 text-xs">
        However, unauthorized access to the deeper Jedi Archives is restricted. Register your biometric signature to unlock the full simulator and save your history.
      </span>
        </p>

        <div class="flex flex-col gap-3">
          <button
              @click="showGuestLimitModal = false; showAuthModal = true; isSignup = true"
              class="w-full bg-cyan-600 text-white py-4 rounded-xl font-black shadow-[0_0_15px_rgba(6,182,212,0.5)] hover:bg-cyan-500 hover:scale-[1.02] transition-all uppercase tracking-widest text-sm"
          >
            Create Account
          </button>

          <button
              @click="showGuestLimitModal = false; goBackToLanding()"
              class="w-full bg-slate-800 text-slate-400 py-4 rounded-xl font-bold border border-slate-700 hover:bg-slate-700 hover:text-white transition-all uppercase tracking-widest text-xs"
          >
            Return to Base
          </button>
        </div>

      </div>
    </div>

    <div v-if="showReviewModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-[300] flex items-center justify-center p-4">
      <div class="bg-white rounded-[2rem] w-full max-w-2xl max-h-[80vh] overflow-hidden shadow-2xl relative flex flex-col">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
          <div>
            <h2 class="text-2xl font-black text-slate-800">Exam Review</h2>
            <p class="text-xs text-slate-500 uppercase tracking-widest font-bold">Analyzing Missed Concepts</p>
          </div>
          <button @click="showReviewModal = false" class="text-slate-400 hover:text-slate-600 text-3xl">&times;</button>
        </div>
        <div class="overflow-y-auto p-6 space-y-8">
          <div v-for="(q, index) in reviewQuestions" :key="q.id" class="review-question-block p-4 border-b">
            <div class="flex justify-between items-center mb-2">
              <h3 class="font-bold">Question {{ index + 1 }}</h3>
              <span :class="isUserCorrect(q.id) ? 'text-green-600' : 'text-red-600'" class="font-bold uppercase text-sm">{{ isUserCorrect(q.id) ? '✓ Correct' : '✗ Incorrect' }}</span>
            </div>
            <p class="mb-4">{{ q.questionText }}</p>
            <div class="bg-blue-50 p-3 rounded text-sm text-blue-800"><strong>Explanation:</strong> {{ q.explanation }}</div>
          </div>
        </div>
        <div class="p-6 border-t border-slate-100 bg-white flex gap-3">
          <button @click="handlePrint" class="flex-1 bg-slate-800 text-white py-3 rounded-xl font-bold shadow-lg">📄 Export to PDF</button>
          <button @click="showReviewModal = false" class="flex-1 bg-blue-600 text-white py-3 rounded-xl font-bold shadow-lg">Got it!</button>
        </div>
      </div>
    </div>

    <div v-if="showSuccessHologram" class="fixed inset-0 z-[500] flex items-center justify-center bg-slate-900/90 backdrop-blur-md">
      <div class="relative bg-black border-2 border-cyan-400 shadow-[0_0_50px_rgba(34,211,238,0.4)] rounded-[2.5rem] p-12 max-w-lg text-center overflow-hidden">
        <div class="absolute inset-0 bg-[linear-gradient(transparent_0%,rgba(34,211,238,0.05)_50%,transparent_100%)] bg-[length:100%_4px] animate-[scan_2s_linear_infinite]"></div>
        <div class="relative z-10">
          <div class="text-6xl mb-6 animate-bounce">✨</div>
          <h2 class="text-cyan-400 font-mono font-black text-2xl mb-4 tracking-[0.2em] uppercase italic">Identity Initialized</h2>
          <p class="text-white font-mono text-lg leading-relaxed mb-8 italic opacity-90">"{{ welcomeMessage }}"</p>
          <div class="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden mb-4">
            <div class="h-full bg-cyan-400 shadow-[0_0_15px_#22d3ee] animate-[loading_5s_linear]"></div>
          </div>
          <p class="text-cyan-600 text-[10px] font-black uppercase tracking-[0.3em] animate-pulse">Syncing with Jedi Archives...</p>
        </div>
      </div>
    </div>

  </div>

  <div id="final-pdf-report" class="hidden print:block p-10 font-serif">
    <div class="flex justify-between items-baseline border-b-2 border-slate-900 pb-2 mb-6">
      <div class="flex items-baseline gap-4">
        <span class="text-2xl font-black text-blue-600 italic">AWS.Hub</span>
        <span class="text-sm font-bold">{{ currentUser?.fullName || 'Exam Candidate' }}</span>
      </div>
      <div class="text-[10px] font-bold uppercase tracking-widest">
        {{ scorePercentage }}% | {{ scorePercentage >= 70 ? 'PASS' : 'FAIL' }}
      </div>
    </div>

    <div class="mb-6 text-[10px] text-slate-500 uppercase">
      Attempted: {{ Math.max(allQuestionsInSession.length, 1) }} Questions | Date: {{ new Date().toLocaleDateString() }}
    </div>

    <div v-for="(q, index) in reviewQuestions" :key="q.id" class="pdf-question-block mb-8">
      <div class="flex justify-between items-center mb-2">
        <h3 class="font-bold text-lg">Question {{ index + 1 }}</h3>
        <span class="text-[9px] font-bold" :class="isUserCorrect(q.id) ? 'text-green-600' : 'text-red-600'">
         {{ isUserCorrect(q.id) ? '✓ CORRECT' : '✗ INCORRECT' }}
       </span>
      </div>
      <p class="mb-4 text-slate-800">{{ q.questionText }}</p>
      <div class="bg-slate-50 p-4 rounded-lg border border-slate-200">
        <p class="text-[9px] font-black text-blue-600 uppercase mb-1">Detailed Explanation</p>
        <p class="text-sm italic leading-relaxed text-slate-600">{{ q.explanation }}</p>
      </div>
    </div>
  </div>

</template>

<script setup>
import confetti from 'canvas-confetti';
import { ref, onMounted, computed, watch, onUnmounted } from 'vue';
import {data} from "autoprefixer";

// --- Core State ---
const question = ref(null);
const selectedAnswers = ref([]);
const feedback = ref('');
const feedbackClass = ref('');
const showExplanation = ref(false);
const currentQuestion = ref(null);
const questions = ref([]);
const missedQuestionIds = ref([]); // Tracks IDs for the Review Room

// --- Exam & Session Logic ---
const sessionCount = ref(1);
const totalExamQuestions = ref(9);
const seenQuestionIds = ref([]);
const allQuestionsInSession = ref([]); // Added this to prevent "undefined" errors
const selectedAnswersRecord = ref({}); // Added this to prevent "undefined" errors
const MAX_HISTORY = 30;
const authError = ref('');

// --- Performance Tracking ---
const showResults = ref(false);
const startTime = ref(null);
const totalExamTime = ref(0);
const categoryScores = ref({});

// --- Platform State ---
const currentView = ref('landing');
const selectedCert = ref(null);
const showGuestLimitModal = ref(false);
const hallOfFame = ref([]);

// --- Timer Logic ---
const timer = ref(0);

// --- Category Logic ---
const selectedCategory = ref('All');
const categories = ['All', 'Data Engineering', 'Algorithms', 'Modeling', 'Deployment', 'Security', 'AI Services'];

// --- Exam History ---
const examHistory = ref([]);

// --- User Login ---
const currentUser = ref(null);
const showAuthModal = ref(true);
const isSignup = ref(true);
const authForm = ref({ email: '', username: '', password: '', fullName: '' });

// --- Review & Print ---
const showReviewModal = ref(false);

const reviewQuestions = ref([]);

const allExamQuestions = ref([]);

const showSuccessHologram = ref(false);
const welcomeMessage = ref('');
const lastAttemptId = ref(null);

const timerSeconds = ref(0);
let timerInterval = null;

const printReview = () => {
  window.print();
};

const openReviewRoom = async (attemptId) => {
  // 1. Immediately clear old data so the user doesn't see previous results
  reviewQuestions.value = [];

  if (!attemptId) {
    console.error("Cannot open review: Attempt ID is missing.");
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/api/questions/history/${attemptId}`);
    if (response.ok) {
      const data = await response.json();
      reviewQuestions.value = Array.isArray(data) ? data : [];
      showReviewModal.value = true; // This opens the review room modal
    }
  } catch (error) {
    console.error("Review Room Error:", error);
  }
};

const resetSession = () => {
  // --- Existing Trackers ---
  allQuestionsInSession.value = [];
  missedQuestionIds.value = [];
  selectedAnswersRecord.value = {};

  // --- Existing UI States ---
  reviewQuestions.value = [];
  lastAttemptId.value = null;

  // --- ADD THESE: Reset the current quiz view ---
  question.value = null;          // Clears the current question from the screen
  selectedAnswers.value = [];     // Unchecks any radio buttons/checkboxes
  showExplanation.value = false;  // Hides the previous explanation
  feedback.value = "";            // Clears "Correct/Incorrect" text
  showResults.value = false;      // Closes the results overlay

  // If you are using a manual counter instead of computed:
  sessionCount.value = 0;
};

const scorePercentage = computed(() => {
  // Use Object.keys to count how many questions the user actually SUBMITTED
  const attemptedIds = Object.keys(selectedAnswersRecord.value);
  const attemptedCount = Object.keys(selectedAnswersRecord.value || {}).length;

  if (attemptedCount === 0) return 0;


  const totalAttempted = attemptedIds.length;

  if (totalAttempted === 0) return 0;

  return Math.round((correctCount.value / attemptedCount) * 100);
});

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
  // Always clear existing intervals first
  if (timerInterval) clearInterval(timerInterval);
  timerSeconds.value == 0;

  timerInterval = setInterval(() => {
    // Use .value to ensure Vue sees the change
    timerSeconds.value++;

  }, 1000);
};


const stopTimer = () => {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
};

const formattedTime = computed(() => {
  // Accessing .value here is what tells Vue to update the HTML
  const totalSeconds = timerSeconds.value;
  const mins = Math.floor(totalSeconds / 60);
  const secs = totalSeconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
});

const finalScore = computed(() => {
  return Math.round((correctCount.value / Math.max(sessionCount.value, 1)) * 100);
});

// The Star Wars message engine
const examResultMessage = computed(() => {
  if (finalScore.value >= 72) {
    return {
      title: "Victory, you have achieved.",
      text: "The Force is strong with this one. You have successfully navigated the asteroid field of cloud architecture and achieved the rank of Master. The Jedi Archives have permanently recorded your victory.",
      color: "text-emerald-500",
      icon: "🌟"
    };
  } else {
    return {
      title: "The greatest teacher, failure is.",
      text: "Do not be discouraged. Even Luke didn't lift the X-Wing on his first try. Review your missed concepts in the archives below, trust your training, and return when your mind is clear to conquer this trial.",
      color: "text-blue-500",
      icon: "🧘‍♂️"
    };
  }
});

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
  if (!selectedCert.value || !selectedCategory.value) return;

  try {
    const url = `http://localhost:8080/api/questions/random?examCode=${selectedCert.value.code}&category=${encodeURIComponent(selectedCategory.value)}&userId=${currentUser.value?.id || ''}&t=${Date.now()}`;

    const response = await fetch(url);
    const data = await response.json();

    // 1. Set the current question
    question.value = data;

    // 2. Update the session array FIRST
    allQuestionsInSession.value.push(data);

    // 3. NOW check the length. It will be 1, so the timer WILL start.
    if (allQuestionsInSession.value.length === 1) {
      startTimer();
    }
  } catch (error) {
    console.error("Fetch Error:", error);
  }
};

const loadNextQuestion = async () => {
  // 1. THE GUEST TRIPWIRE
  // Halts the sequence if no user is authenticated and they hit the 10-question limit
  if (!currentUser.value && sessionCount.value >= 10) {
    if (timerInterval) clearInterval(timerInterval);
    showGuestLimitModal.value = true;
    return;
  }

  // 2. THE EXAM FINISH CHECK
  // If they reach the end of the total exam questions
  if (sessionCount.value >= totalExamQuestions.value) {
    // Route it through our new, resilient grading engine
    forceGradeExam();
    return;
  }

  // 3. PREPARE THE NEXT CYCLE
  sessionCount.value++;
  showExplanation.value = false;
  selectedAnswers.value = [];
  if (typeof feedback !== 'undefined') feedback.value = '';

  // 4. FETCH THE NEW DATA
  await fetchQuestion();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const checkAnswer = () => {
  if (!question.value) return;

  // 1. Record the user's pick immediately
  selectedAnswersRecord.value[question.value.id] = [...selectedAnswers.value];

  const correctIds = question.value.options
      .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
      .map(opt => String(opt.id));

  const selectedIds = selectedAnswers.value.map(id => String(id));
  const isCorrect = selectedIds.length === correctIds.length &&
      selectedIds.every(id => correctIds.includes(id));

  // 2. CRITICAL: Update the missed list for the Review Room
  if (!isCorrect) {
    if (!missedQuestionIds.value.includes(question.value.id)) {
      missedQuestionIds.value.push(question.value.id);
    }
  }

  showExplanation.value = true;
  feedback.value = isCorrect ? "Correct!" : "Incorrect";
  feedbackClass.value = isCorrect ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700";
};

const saveResults = async () => {
  try {
    const historyData = {
      userId: currentUser.value?.id,
      examCode: selectedCert.value?.code || 'AWS-CERT',
      scorePercentage: Math.round((correctCount.value / allQuestionsInSession.value.length) * 100),
      totalQuestions: allQuestionsInSession.value.length,
      correctCount: correctCount.value,
      // Convert the array [10, 22] into a string "10,22" for the database
      missedQuestionIds: missedQuestionIds.value.join(','),
      attemptDate: new Date().toISOString()
    };

    const response = await fetch('http://localhost:8080/api/questions/history/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(historyData)
    });

    if (response.ok) {
      const data = await response.json();
      lastAttemptId.value = data.id; // Unlocks the Review button
      showResults.value = true;
    }
  } catch (err) {
    console.error("Save Failure:", err);
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

const fetchHallOfFame = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/questions/history/leaderboard');
    if (response.ok) {
      hallOfFame.value = await response.json();
    }
  } catch (error) {
    console.error("Could not load Hall of Fame:", error);
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

const startQuiz = async () => {
  const response = await fetch('http://localhost:8080/api/questions/all');
  questions.value = await response.json();
  currentQuestion.value = questions.value[0]; // Now it's defined!
  currentView.value = 'quiz';
};

// 1. Add the Certifications list
const certifications = ref([
  {
    code: 'MLS-C01',
    name: 'Machine Learning Specialty',
    image: 'https://d1.awsstatic.com/training-and-certification/certification-badges/AWS-Certified-Machine-Learning-Specialty_badge.png',
    color: 'bg-purple-500', icon: '🤖'
  },
  {
    code: 'AIF-C01',
    name: 'AI Practitioner',
    image: 'https://images.credly.com/images/154fa414-0683-4a12-a72d-7bb432ecff1c/image.png',
    color: 'bg-amber-400', icon: '✨'
  },
  {
    code: 'SCS-C02',
    name: 'Security Specialty',
    image: 'https://d1.awsstatic.com/training-and-certification/certification-badges/AWS-Certified-Security-Specialty_badge.png',
    color: 'bg-red-500', icon: '🛡️'
  },
  {
    code: 'SAA-C03',
    name: 'Solutions Architect',
    image: 'https://d1.awsstatic.com/training-and-certification/certification-badges/AWS-Certified-Solutions-Architect-Associate_badge.png',
    color: 'bg-blue-500', icon: '🏗️'
  },
  {
    code: 'DEA-C01',
    name: 'Data Engineering',
    image: 'https://images.credly.com/images/ce55938f-5b12-4217-a065-efc4eaf03eb6/image.png',
    color: 'bg-teal-500', icon: '🗄️'
  }
]);

// 2. Add the Leaderboard ref back
const leaderboard = ref([]);

// --- 2. The Selection Logic ---
const selectCertification = async (cert) => {
  // 1. Lock in the selected certification
  selectedCert.value = cert;

  // Choose ONE default category (I'll use 'All' based on your snippet)
  selectedCategory.value = 'All';

  // 2. Fetch the total count of questions for this cert
  if (typeof fetchTotalCount === 'function') {
    await fetchTotalCount();
  }

  // 3. Swap the HMI screen
  currentView.value = 'quiz';

  // 4. Wipe the previous session memory cleanly
  allQuestionsInSession.value = [];
  sessionCount.value = 1;
  showResults.value = false;
  isPaused.value = false;
  showExplanation.value = false;
  selectedAnswers.value = [];
  if (typeof authError !== 'undefined') authError.value = '';

  // 5. Reset and start the clock
  // 5. Reset and start the clock
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
  timerSeconds.value = 0;
  timerInterval = setInterval(() => { timerSeconds.value++; }, 1000);

  // 6. Fire the sequence! (Call fetchQuestion directly instead of resetSession)
  try {
    await fetchQuestion();
  } catch (error) {
    console.error("Failed to load first question:", error);
    // If it fails, kick them back to the landing page safely
    currentView.value = 'landing';
    alert("Could not load questions for this certification. Please check your backend connection.");
  }
};

const fetchLeaderboard = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/questions/leaderboard');
    if (response.ok) {
      const data = await response.json();

      leaderboard.value = data;
    }
  } catch (error) {
    console.error("Leaderboard fetch error:", error);
  }
};

// --- 4. The Auth Logic (Login/Signup) ---
const handleAuth = async () => {
  // 1. Clear any old errors the moment they click the button
  authError.value = '';

  const endpoint = isSignup.value ? '/register' : '/login';

  try {
    const response = await fetch(`http://localhost:8080/api/auth${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(authForm.value)
    });

    // 2. The Star Wars Error Interceptor
    if (!response.ok) {
      // Clear the password field for security so they have to re-type it
      authForm.value.passwordhash = '';

      if (response.status === 401) {
        authError.value = "You shall not pass! (Er, I mean, invalid password...)";
        return;
      } else if (response.status === 400 || response.status === 404) {
        authError.value = "These aren't the credentials you're looking for.";
        return;
      }
      throw new Error(`Server error: ${response.status}`);
    }

    // 3. Success! Log them in and close the modal.
    const userData = await response.json();
    currentUser.value = userData;
    showAuthModal.value = false;

    // (Optional) If you have the Jedi hologram from earlier, uncomment the line below:
    // showSuccessHologram.value = true;

  } catch (error) {
    console.error("Auth Error:", error);
    authForm.value.passwordhash = ''; // Clear password on server crash too
    authError.value = "Communication disruption can mean only one thing: invasion. (Server offline)";
  }
};

const toggleAnswer = (optionId) => {
  // Guard: If no question is loaded or explanation is showing, do nothing
  if (!question.value || showExplanation.value) return;

  // Identify multi-select questions by counting 'isCorrect' flags
  const correctOptions = question.value.options.filter(o =>
      o.isCorrect === true || String(o.isCorrect).toLowerCase() === 'true'
  );

  const maxAllowed = correctOptions.length > 1 ? correctOptions.length : 1;

  if (maxAllowed === 1) {
    selectedAnswers.value = [optionId];
  } else {
    const index = selectedAnswers.value.indexOf(optionId);
    if (index > -1) {
      selectedAnswers.value.splice(index, 1);
    } else if (selectedAnswers.value.length < maxAllowed) {
      selectedAnswers.value.push(optionId);
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

const exportToPDF = () => {
  // We trigger the browser print dialog
  window.print();
};

const forceGradeExam = async () => {
  // 1. The Confirmation Guard
  if (!confirm("Are you sure you want to end the exam now and see your grade?")) {
    return;
  }

  // 2. Stop the clock
  if (timerInterval) {
    clearInterval(timerInterval); // Stops the counting
    timerInterval = null;         // Destroys the ghost reference
  }
  isPaused.value = false;

  // --- 3. THE MATH FIX ---
  // If 'showExplanation' is false, they haven't submitted an answer for the question on screen.
  // We pop it out of the array so it doesn't count against them!
  if (!showExplanation.value && allQuestionsInSession.value.length > 0) {
    allQuestionsInSession.value.pop();
  }

  // Now sync the counters to match ONLY the questions they actually submitted
  sessionCount.value = Math.max(allQuestionsInSession.value.length, 1);
  totalExamQuestions.value = sessionCount.value;

  // 4. INSTANT UI UPDATE
  showResults.value = true;
  window.scrollTo({ top: 0, behavior: 'smooth' });

  // 5. Build the payload (now it only contains the answered questions)
  const answerPayload = allQuestionsInSession.value.map(q => ({
    questionId: q.id,
    category: q.category,
    correct: isUserCorrect(q.id)
  }));

  const payload = {
    userId: currentUser.value?.id,
    examCode: selectedCert.value?.code || 'UNKNOWN',
    answers: answerPayload
  };

  // 6. Send to server silently in the background
  try {
    const response = await fetch('http://localhost:8080/api/questions/history/grade', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      const savedRecord = await response.json();
      if (savedRecord && savedRecord.id) {
        lastAttemptId.value = savedRecord.id;
      }
    }

    // Refresh the Hall of Fame and Personal History behind the scenes
    setTimeout(() => {
      if (typeof fetchHistory === 'function') fetchHistory();
      if (typeof fetchHallOfFame === 'function') fetchHallOfFame();
    }, 500);

  } catch (error) {
    console.warn("Background save process encountered a network quirk:", error);
  }
};

const handlePrint = async () => {
  // If the data isn't loaded yet, go get it using the last ID
  if (reviewQuestions.value.length === 0 && lastAttemptId.value) {
    const response = await fetch(`http://localhost:8080/api/questions/history/${lastAttemptId.value}`);
    reviewQuestions.value = await response.json();
  }

  // Brief timeout to let the HTML render before the print dialog opens
  setTimeout(() => {
    window.print();
  }, 500);
};

const getFormattedDate = () => {
  return new Date().toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  });
};

const triggerFirework = () => {
  // ... existing code ...
  const defaults = {
    startVelocity: 30,
    spread: 360,
    ticks: 60,
    zIndex: 9999 // <--- Add this to put fireworks on top of everything!
  };
  // ... rest of function ...
};

const isUserCorrect = (questionId) => {
  const q = allQuestionsInSession.value.find(quest => quest.id === questionId);
  if (!q) return false;

  const correctIds = q.options
      .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
      .map(opt => String(opt.id));

  const userIds = (selectedAnswersRecord.value[questionId] || []).map(id => String(id));

  return correctIds.length === userIds.length &&
      correctIds.every(id => userIds.includes(id));
};

// --- Computed Results ---

// 1. Calculate how many questions were answered perfectly
const correctCount = computed(() => {
  // Safety: If these aren't arrays yet, return 0
  if (!allQuestionsInSession.value || !Array.isArray(allQuestionsInSession.value)) return 0;
  if (allQuestionsInSession.value.length === 0) return 0;

  return allQuestionsInSession.value.filter(q => {
    // Safety: If user never even clicked this question, it's not correct
    if (!selectedAnswersRecord.value || !selectedAnswersRecord.value[q.id]) return false;

    const correctOptionIds = q.options
        .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
        .map(opt => String(opt.id));

    const userSelectedIds = selectedAnswersRecord.value[q.id].map(id => String(id));

    return correctOptionIds.length === userSelectedIds.length &&
        correctOptionIds.every(id => userSelectedIds.includes(id));
  }).length;
});

// 3. Determine if they passed the AWS standard (72%)
const isPassing = computed(() => scorePercentage.value >= 72);

onMounted(async () => {

  // --- 1. THE WATCHER (Must be outside onMounted in Vue 3) ---
  watch(selectedCert, (newCert) => {
    if (newCert && currentUser.value) {
      // Only fetch if we aren't already handling this in selectCertification
      if (typeof fetchQuestion === 'function' && currentView.value === 'quiz') {
        fetchQuestion();
      }
    }
  }, { immediate: true });


// --- 2. THE MOUNT CYCLE ---
  onMounted(async () => {
    // A. Always fetch global Hall of Fame for the dashboard (Everyone sees this)
    if (typeof fetchHallOfFame === 'function') {
      fetchHallOfFame();
    }

    // B. Check local storage for a returning Operative
    const savedUser = localStorage.getItem('aws_user');

    if (savedUser) {
      currentUser.value = JSON.parse(savedUser);
      showAuthModal.value = false;

      // C. Fetch their personal stats now that we know who they are
      if (typeof fetchHistory === 'function') {
        await fetchHistory();
      }

      // D. Resume quiz if they refreshed the page while looking at a cert
      if (selectedCert.value && typeof fetchQuestion === 'function') {
        fetchQuestion();
      }
    }
   });
});

onUnmounted(() => stopTimer());
</script>

<style scoped>
@media print {
  /* 1. Reset: Hide the entire app root and its scrolling containers */
  #app, .min-h-screen, nav, .fixed, .modal-backdrop {
    display: none !important;
  }

  /* 2. Force the report to be the ONLY thing the printer sees */
  #final-pdf-report {
    display: block !important;
    visibility: visible !important;
    position: static !important; /* Forces standard document flow */
    width: 100% !important;
    margin: 0 !important;
    padding: 20mm !important; /* Standard print margins */
    background: white !important;
  }

  /* 3. THE HEADER FIX: Prevents overlap by ensuring it's a block */
  .pdf-header {
    display: block !important;
    margin-bottom: 50px !important; /* Pushes Question 1 down */
    border-bottom: 2px solid #000;
    padding-bottom: 10px;
    page-break-after: avoid; /* Keeps header with first question */
  }

  /* 4. THE REPEAT FIX: Prevents ghosting questions on multiple pages */
  .pdf-question-block {
    display: block !important;
    position: static !important;
    width: 100% !important;

    /* CRITICAL: Use 'avoid' instead of 'always'.
       'Always' is what was triggering the infinite page loop.
    */
    break-inside: avoid !important;
    page-break-inside: avoid !important;

    margin-bottom: 30px !important;
    padding-top: 10px !important;
  }

  /* Ensure text is black for printing */
  * {
    color: black !important;
    text-shadow: none !important;
  }
}

@keyframes scan {
  from { background-position: 0 0; }
  to { background-position: 0 100%; }
}

@keyframes loading {
  0% { width: 0%; }
  100% { width: 100%; }
}

/* Hide the PDF template during normal browsing */
.hidden-on-screen {
  display: none;
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.btn-primary-glow {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.btn-primary-glow:hover {
  transform: translateY(-2px);
  background-color: #2563eb; /* A slightly brighter blue */
  box-shadow: 0 10px 25px -5px rgba(37, 99, 235, 0.4);
}

.btn-primary-glow:active {
  transform: translateY(1px);
}

</style>