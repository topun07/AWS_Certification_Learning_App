<template>
  <div class="min-h-screen bg-gray-50 pb-12 px-4 font-sans relative">

    <!-- Top Navigation Bar -->
    <div class="max-w-5xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4 mb-6 bg-white p-6 md:p-4 rounded-3xl shadow-sm border border-slate-100 mt-6">

    <div class="flex items-center gap-2 px-4 flex-shrink-0">
      <span class="text-3xl md:text-2xl font-black text-blue-600 tracking-tighter italic">AWS.Hub</span>
    </div>

    <div v-if="currentUser" class="flex flex-col md:flex-row items-center gap-3">

      <div class="flex items-center gap-2 px-4 py-2.5 bg-slate-50 rounded-xl border border-slate-200 shadow-sm transition-all hover:shadow-md">
          <span class="text-2xl transition-all duration-500" :class="[streakDisplay.color, streakDisplay.aura]">
            {{ streakDisplay.icon }}
          </span>
        <div class="flex flex-col text-left">
            <span class="text-[10px] font-black uppercase tracking-wider text-slate-400 leading-none mb-1">
              Daily Streak
            </span>
          <span class="text-sm font-bold text-slate-800 leading-none">
              {{ userStreak }} Days
              <span class="text-[10px] font-normal italic opacity-75 ml-1 hidden lg:inline-block">
                ({{ streakDisplay.label }})
              </span>
            </span>
        </div>
      </div>

      <div class="flex flex-col bg-white/80 backdrop-blur-md p-3 px-5 rounded-[1.5rem] border border-slate-200 shadow-xl transition-all hover:shadow-blue-100/50 w-full md:w-64">

        <div class="flex items-center justify-between gap-4 mb-2">
          <div class="flex flex-col">
              <span class="text-[10px] font-black uppercase tracking-[0.2em] mb-0.5" :class="userRank.color">
                {{ userRank.title }}
              </span>
            <span class="text-xl font-black text-slate-900 tracking-tighter leading-none drop-shadow-sm truncate max-w-[120px]">
                {{ currentUser?.username }}
              </span>
          </div>

          <div class="h-8 w-px bg-slate-200 hidden md:block"></div>

          <button @click="logout" class="group relative flex flex-col items-center">
            <span class="text-[9px] font-black text-red-500 uppercase tracking-widest transition-all group-hover:text-red-700">Logout</span>
            <span class="h-0.5 w-0 bg-red-500 transition-all group-hover:w-full"></span>
          </button>
        </div>

        <div class="w-full">
          <div class="flex justify-between text-[9px] font-black text-slate-400 mb-1 uppercase tracking-widest">
            <span>{{ userTotalXp }} XP</span>
            <span>{{ userRank.max }} XP</span>
          </div>
          <div class="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden shadow-inner">
            <div
                class="h-full rounded-full transition-all duration-1000 ease-out shadow-[inset_0_1px_3px_rgba(255,255,255,0.3)]"
                :class="userRank.bar"
                :style="`width: ${userRank.progress}%`"
            ></div>
          </div>
        </div>

      </div>

      <button @click="toggleMusic" class="p-2 rounded-full hover:bg-slate-100 transition-colors group" :title="isMusicPlaying ? 'Pause Focus Track' : 'Play Focus Track'">
          <span class="text-lg opacity-70 group-hover:opacity-100 transition-opacity" :class="isMusicPlaying ? 'animate-pulse text-blue-500' : 'grayscale'">
            🎧
          </span>
      </button>

      <button @click="isAudioEnabled = !isAudioEnabled" class="p-2 rounded-full hover:bg-slate-100 transition-colors group" :title="isAudioEnabled ? 'Mute SFX' : 'Enable SFX'">
          <span class="text-lg opacity-70 group-hover:opacity-100 transition-opacity">
            {{ isAudioEnabled ? '🔊' : '🔇' }}
          </span>
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

    <div v-if="showAuthModal" class="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50">
      <div class="bg-gray-900 border border-blue-500 rounded-lg p-8 w-96 text-white shadow-[0_0_15px_rgba(59,130,246,0.5)]">

        <h2 class="text-2xl font-bold text-center mb-6 text-blue-400">
          {{ isLoginMode ? 'Access Datapad' : 'Register Recruit' }}
        </h2>

        <div v-if="authError" class="bg-red-900 border border-red-500 text-red-200 p-2 rounded mb-4 text-sm text-center">
          {{ authError }}
        </div>

        <form @submit.prevent="submitAuth" class="space-y-4">

          <div>
            <label class="block text-sm text-gray-400 mb-1">Codename (Username)</label>
            <input v-model="authForm.username" type="text" required class="w-full bg-gray-800 border border-gray-600 rounded p-2 text-white focus:outline-none focus:border-blue-500" />
          </div>

          <div v-if="!isLoginMode">
            <label class="block text-sm text-gray-400 mb-1">Comm-Link (Email)</label>
            <input v-model="authForm.email" type="email" required class="w-full bg-gray-800 border border-gray-600 rounded p-2 text-white focus:outline-none focus:border-blue-500" />
          </div>

          <div>
            <label class="block text-sm text-gray-400 mb-1">Passcode</label>
            <input v-model="authForm.password" type="password" required class="w-full bg-gray-800 border border-gray-600 rounded p-2 text-white focus:outline-none focus:border-blue-500" />
          </div>

          <button type="submit" class="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold py-2 px-4 rounded transition-colors mt-4">
            {{ isLoginMode ? 'Login' : 'Create Account' }}
          </button>

        </form>

        <div class="text-center mt-6">
          <button @click="isLoginMode = !isLoginMode; authError = ''" class="text-sm text-blue-400 hover:text-blue-300 underline">
            {{ isLoginMode ? 'Need an account? Register here.' : 'Already a recruit? Login here.' }}
          </button>
        </div>

        <button @click="showAuthModal = false" class="absolute top-4 right-4 text-gray-400 hover:text-white text-xl">
          ✕
        </button>
      </div>
    </div>

    <div v-if="selectedCert && currentView === 'landing'" id="protocol-menu" class="mt-8 mb-16 bg-white rounded-[2rem] p-6 md:p-8 border border-slate-200 shadow-xl animate-fade-in-up flex flex-col md:flex-row gap-8 max-w-5xl mx-auto">

      <div class="flex-shrink-0 flex flex-col items-center justify-center bg-slate-50 p-8 rounded-[1.5rem] border border-slate-100 md:w-1/3">
        <div :class="[selectedCert.color, 'w-24 h-24 rounded-2xl shadow-lg flex items-center justify-center text-5xl mb-4 transform transition-transform hover:scale-105']">
          {{ selectedCert.icon }}
        </div>
        <h3 class="text-xl font-black text-slate-900 text-center leading-tight mb-1">{{ selectedCert.name }}</h3>
        <span class="text-xs font-bold px-3 py-1 bg-slate-200 text-slate-600 rounded-full uppercase tracking-wider mb-6">{{ selectedCert.code }}</span>

        <button @click="selectedCert = null" class="mt-auto text-[10px] font-black text-red-500 hover:text-red-700 transition-colors uppercase tracking-widest flex items-center gap-1 group">
          <span class="group-hover:-translate-x-1 transition-transform">←</span> Change Target
        </button>
      </div>

      <div class="flex-grow flex flex-col gap-3">
        <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1 px-2">Select Training Protocol</h4>

        <button @click="initiateProtocol('standard')" class="group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-blue-500 hover:shadow-md transition-all text-left w-full">
          <span class="text-2xl bg-blue-50 text-blue-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-blue-500 group-hover:text-white transition-all">🏛️</span>
          <div>
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-blue-600 transition-colors">Simulated Exam</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">Standard assessment. Timed. Real exam conditions.</p>
          </div>
        </button>

        <button @click="initiateProtocol('amrap')" class="group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-red-500 hover:shadow-md transition-all text-left w-full">
          <span class="text-2xl bg-red-50 text-red-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-red-500 group-hover:text-white transition-all">⏱️</span>
          <div>
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-red-600 transition-colors">AMRAP Attack</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">10 minute clock. Infinite questions. Pure speed and recall.</p>
          </div>
        </button>

        <button @click="initiateProtocol('sudden_death')" class="group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-purple-500 hover:shadow-md transition-all text-left w-full">
          <span class="text-2xl bg-purple-50 text-purple-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-purple-500 group-hover:text-white transition-all">💀</span>
          <div>
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-purple-600 transition-colors">Sudden Death</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">3 Lives. No timer. One mistake shatters a heart. Survive.</p>
          </div>
        </button>

        <button @click="enterKnowledgeForge" class="group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-emerald-500 hover:shadow-md transition-all text-left w-full">
          <span class="text-2xl bg-emerald-50 text-emerald-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-emerald-500 group-hover:text-white transition-all">🧠</span>
          <div>
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-emerald-600 transition-colors">The Knowledge Forge</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">Access study guides, architectural diagrams, and video modules.</p>
          </div>
        </button>

      </div>
    </div>

    <!-- View: Landing Page -->
    <div v-if="currentView === 'landing'" class="max-w-7xl mx-auto px-4 md:px-8 lg:px-12">

      <div class="text-center mb-12 md:mb-20 mt-8 md:mt-12">
        <h1 class="text-4xl md:text-6xl lg:text-7xl font-extrabold text-gray-900 mb-4 md:mb-6 tracking-tight">AWS Certification Hub</h1>
        <p class="text-lg md:text-2xl lg:text-3xl text-gray-600 font-medium">Select your path and master the cloud.</p>
      </div>

      <div v-for="(certs, categoryName) in groupedCerts" :key="categoryName">

        <div v-if="certs.length > 0" class="mb-12">

          <div class="flex items-center gap-4 mb-8">
            <h2 class="text-2xl font-black text-slate-800 uppercase tracking-widest">{{ categoryName }}</h2>
            <div class="h-px bg-slate-200 flex-grow"></div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            <div
                v-for="cert in certs"
                :key="cert.code"
                @click="selectCertification(cert)"
                class="group cursor-pointer bg-white rounded-[2.5rem] p-8 shadow-sm border border-slate-100 hover:shadow-2xl hover:-translate-y-2 transition-all flex flex-col items-center text-center"
            >
              <div class="h-28 w-28 md:h-40 md:w-40 mb-6 flex items-center justify-center relative">
                <img
                    v-if="cert.image"
                    :src="cert.image"
                    @error="cert.image = null"
                    class="w-full h-full object-contain drop-shadow-2xl group-hover:scale-110 transition-transform duration-300"
                />
                <div v-else :class="['w-full h-full rounded-3xl flex items-center justify-center text-5xl text-white shadow-lg', cert.color]">
                  {{ cert.icon }}
                </div>
              </div>

              <div class="space-y-2">
                <h3 class="font-black text-slate-900 text-xl md:text-2xl leading-tight">{{ cert.name }}</h3>
                <p class="text-blue-600 text-[10px] md:text-xs font-mono font-black uppercase tracking-[0.3em] opacity-60">{{ cert.code }}</p>
              </div>
            </div>
          </div>

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
        <div class="bg-white rounded-[2rem] shadow-xl p-8 border border-slate-100 flex-1">
          <h3 class="text-2xl font-black text-slate-900 mb-6 flex items-center gap-2">
            ✨ Ascended Masters
          </h3>

          <div v-if="leaderboardData.length > 0" class="space-y-3">
            <div v-for="(master, index) in leaderboardData" :key="index"
                 class="flex items-center justify-between p-4 bg-slate-50 border border-slate-100 rounded-xl hover:shadow-md transition-all">

              <div class="flex items-center gap-4">
                <div class="w-8 h-8 rounded-full flex items-center justify-center font-black text-sm shadow-sm"
                     :class="index === 0 ? 'bg-gradient-to-br from-amber-200 to-amber-400 text-amber-900' :
                     index === 1 ? 'bg-gradient-to-br from-slate-200 to-slate-400 text-slate-800' :
                     index === 2 ? 'bg-gradient-to-br from-orange-200 to-orange-400 text-orange-900' :
                     'bg-blue-100 text-blue-600'">
                  #{{ index + 1 }}
                </div>

                <div>
                  <p class="font-black text-slate-800">{{ master.fullName }}</p>
                  <p class="text-[10px] font-bold text-slate-400 tracking-widest uppercase">{{ master.examCode }}</p>
                </div>
              </div>

              <div class="text-xl font-black"
                   :class="master.scorePercentage === 100 ? 'text-emerald-500' : 'text-slate-700'">
                {{ master.scorePercentage }}%
              </div>

            </div>
          </div>

          <div v-else class="text-center py-10">
            <div class="text-5xl mb-4 opacity-50 animate-pulse">☁️</div>
            <p class="text-slate-500 font-bold">No legends yet.</p>
            <p class="text-sm text-slate-400 mt-1">Score 100% to claim the top spot!</p>
          </div>
        </div>
      </div>

      <div class="mt-12 grid grid-cols-1 lg:grid-cols-2 gap-8">

        <div class="bg-white rounded-[2rem] p-8 shadow-sm border border-slate-100 flex flex-col h-full">

          <div class="mb-6 flex justify-between items-start">
            <div>
              <h3 class="text-2xl font-black text-slate-800">Skill Radar</h3>
              <p class="text-slate-500 text-sm mt-1">Your mastery across architectural domains.</p>
            </div>

            <select v-model="chartViewCode" class="bg-slate-50 border border-slate-200 text-slate-700 text-sm rounded-xl focus:ring-blue-500 focus:border-blue-500 block p-2.5 font-bold outline-none cursor-pointer hover:bg-slate-100 transition-colors">
              <option value="DEFAULT">Overall AWS</option>

              <optgroup label="Foundational">
                <option value="CLF-C02">Cloud Practitioner (CLF-C02)</option>
                <option value="AIF-C01">AI Practitioner (AIF-C01)</option>
              </optgroup>

              <optgroup label="Associate">
                <option value="SAA-C03">Solutions Architect (SAA-C03)</option>
                <option value="DVA-C02">Developer (DVA-C02)</option>
                <option value="SOA-C02">SysOps Admin (SOA-C02)</option>
                <option value="DEA-C01">Data Engineering (DEA-C01)</option>
              </optgroup>

              <optgroup label="Professional">
                <option value="SAP-C02">Solutions Architect Pro (SAP-C02)</option>
                <option value="DOP-C02">DevOps Engineer Pro (DOP-C02)</option>
                <option value="AIP-C01">Generative AI Dev (AIP-C01)</option>
              </optgroup>

              <optgroup label="Specialty">
                <option value="MLS-C01">Machine Learning (MLS-C01)</option>
                <option value="SCS-C02">Security (SCS-C02)</option>
                <option value="ANS-C01">Advanced Networking (ANS-C01)</option>
              </optgroup>
            </select>
          </div>

          <div class="flex-grow relative min-h-[300px] w-full flex justify-center items-center">
            <Radar :key="chartViewCode" :data="radarData" :options="radarOptions" />
          </div>
        </div>

        <div class="bg-white rounded-[2rem] p-8 shadow-sm border border-slate-100 flex flex-col h-full">

          <div class="mb-6">
            <h3 class="text-2xl font-black text-slate-800">Target Priorities</h3>
            <p class="text-slate-500 text-sm mt-1">Recommended focus areas based on recent telemetry.</p>
          </div>

          <div class="flex-grow flex flex-col justify-center space-y-4">

            <div v-if="studyPriorities.length > 0" class="flex flex-col gap-3">

              <div
                  v-for="(priority, index) in studyPriorities"
                  :key="priority.name"
                  @click="jumpToForge(priority.name)"
                  :class="index === 0 ? 'bg-red-50 border border-red-100 text-red-900 hover:bg-red-100' : 'bg-amber-50 border border-amber-100 text-amber-900 hover:bg-amber-100'"
                  class="flex items-center justify-between p-4 rounded-xl shadow-sm transition-all hover:shadow-md cursor-pointer hover:-translate-y-1 group"
                  title="Click to study this module in The Knowledge Forge"
              >
                <div class="flex items-center gap-3">
                  <span class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-black transition-transform group-hover:scale-110" :class="index === 0 ? 'bg-red-200 text-red-700' : 'bg-amber-200 text-amber-700'">!</span>
                  <span class="font-bold text-sm group-hover:underline">{{ priority.name }}</span>
                </div>
                <div class="flex items-center gap-4">
                  <span class="font-black text-lg" :class="index === 0 ? 'text-red-600' : 'text-amber-600'">{{ priority.percentage }}%</span>
                  <span class="opacity-0 group-hover:opacity-100 transition-opacity font-bold text-xl" :class="index === 0 ? 'text-red-500' : 'text-amber-500'">➔</span>
                </div>
              </div>

            </div>

            <div v-else class="text-center p-6 text-slate-400 text-sm font-bold">
              Insufficient telemetry data. Complete a simulation to generate priorities.
            </div>

          </div>
        </div>

      </div>

      <div class="bg-slate-50 rounded-[2rem] p-8 border border-slate-100 border-dashed flex flex-col items-center justify-center text-center opacity-70 mt-8">
        <div class="text-4xl mb-4">🎯</div>
        <h3 class="text-xl font-bold text-slate-400">Study Priorities</h3>
        <p class="text-sm text-slate-400 mt-2">More analytics incoming...</p>
      </div>

      <div class="mt-12 bg-white rounded-3xl p-8 border border-slate-100 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6">
        <div>
          <h3 class="text-xl font-black text-slate-800">Database Management</h3>
          <p class="text-slate-500 text-sm mt-1">Expand the Jedi Archives. Upload new AWS question banks via CSV.</p>
        </div>

        <div>
          <input
              type="file"
              id="csv-upload"
              accept=".csv"
              class="hidden"
              @change="handleFileUpload"
          />
          <label
              for="csv-upload"
              class="cursor-pointer inline-flex items-center justify-center gap-2 bg-slate-800 hover:bg-slate-950 text-white px-8 py-4 rounded-xl font-black transition-all shadow-lg hover:shadow-xl hover:-translate-y-1"
          >
            <svg class="w-5 h-5 text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
            Upload CSV Database
          </label>
        </div>
      </div>

    </div>

    <!-- View: Quiz / Results -->
    <div v-else-if="currentView === 'quiz'" class="max-w-5xl mx-auto px-4 md:px-8 lg:px-12">

      <!-- Results Screen -->
      <div v-if="showResults && !isHistoricalView" class="bg-white rounded-[2rem] shadow-2xl p-10 text-center border border-gray-100">
        <div class="mb-10 text-center flex flex-col items-center">
          <div class="mb-8 flex justify-center w-full px-2">
            <img
                v-if="examResultMessage.image"
                :src="examResultMessage.image"
                class="w-full max-w-sm md:max-w-2xl h-auto object-cover rounded-2xl shadow-2xl border border-slate-100/50"
                alt="Result Animation"
            />
            <div v-else class="text-7xl md:text-8xl">{{ examResultMessage.icon }}</div>
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
          <div class="bg-amber-50 p-6 rounded-2xl text-center flex flex-col justify-center border border-amber-100 shadow-inner">
            <div class="mb-3">
              <p class="text-amber-600 text-[10px] font-black uppercase tracking-widest mb-1">Total Time</p>
              <p class="text-3xl font-black text-amber-900">{{ formattedTotalTime }}</p>
            </div>
            <div class="h-px bg-amber-200/50 w-full mb-3"></div>
            <div>
              <p class="text-amber-600 text-[10px] font-black uppercase tracking-widest mb-1">Avg per Question</p>
              <p class="text-xl font-black text-amber-800">{{ formattedAverageTime }}</p>
            </div>
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
      <div v-else-if="question && !showReviewModal" class="bg-white rounded-[2rem] shadow-xl overflow-hidden border border-slate-200 relative">
        <div v-if="isPaused" class="absolute inset-0 bg-white/95 backdrop-blur-md z-50 flex flex-col items-center justify-center text-center p-8">
          <div class="w-16 h-16 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center text-3xl mb-4">⏸️</div>
          <h2 class="text-2xl font-black text-slate-800">Exam Paused</h2>

          <button @click="togglePause" class="mt-6 bg-slate-800 text-white px-8 py-3 rounded-2xl font-bold">Resume Exam</button>
        </div>

        <div class="bg-blue-600 p-6 flex justify-between items-center text-white">
          <div>
            <p class="text-[10px] font-black uppercase tracking-widest opacity-70">{{ question.category }}</p>

            <h2 v-if="!isAmrapMode" class="text-lg font-bold">Question {{ sessionCount }} of {{ totalExamQuestions }}</h2>

            <h2 v-else class="text-lg font-bold">Reps Completed: {{ amrapCorrectCount }}</h2>

          </div>

          <div class="flex items-center gap-4">

            <div v-if="isSuddenDeath" class="flex items-center gap-1.5 bg-slate-900/40 px-3 py-1.5 rounded-xl shadow-inner border border-white/10">
        <span
            v-for="n in 3"
            :key="n"
            class="text-xl transition-all duration-500 ease-[cubic-bezier(0.34,1.56,0.64,1)]"
            :class="n <= healthPoints ? 'text-red-500 scale-100 drop-shadow-[0_0_8px_rgba(239,68,68,0.8)]' : 'opacity-20 scale-75 grayscale'"
        >
          ❤️
        </span>
            </div> <div v-else-if="isAmrapMode" class="flex items-center gap-2 bg-red-600/20 px-4 py-1.5 rounded-xl border border-red-500/50 shadow-[0_0_15px_rgba(220,38,38,0.3)]">
            <span class="text-red-500 animate-pulse">⏱️</span>
            <span class="font-mono font-black text-red-100 tracking-widest text-lg">{{ formattedAmrapTime }}</span>
          </div> <span v-else class="font-mono font-bold bg-blue-500/50 px-3 py-1 rounded-lg border border-blue-400/50">
      {{ formattedCurrentTime }}
    </span> <button @click="togglePause" class="bg-white text-blue-600 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg">{{ isPaused ? 'Resume' : 'Pause' }}</button>
            <button @click="showGradeConfirmModal = true" class="bg-slate-900/40 hover:bg-slate-900/60 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg transition-colors border border-white/10">
              🏁 Grade Now
            </button>
          </div>
        </div>

        <div class="p-8">
          <p class="text-xl md:text-3xl lg:text-3xl font-extrabold text-slate-800 mb-8 md:mb-12 leading-relaxed md:leading-tight">
            {{ question.questionText }}
          </p>
                <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)"
                        :class="['w-full text-left p-4 md:p-6 lg:p-8 rounded-2xl md:rounded-[2rem] border-2 transition-all flex items-center gap-4 md:gap-6', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50' : 'border-slate-100 bg-slate-50 hover:border-blue-200 hover:shadow-md']">

                  <div class="w-6 h-6 rounded border-2 flex items-center justify-center flex-shrink-0" :class="selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-500 text-white' : 'border-slate-300 bg-white'">
                    <span v-if="selectedAnswers.includes(opt.id)">✓</span>
                  </div>

                  <span class="text-base md:text-lg lg:text-xl font-bold text-slate-700">
              {{ opt.text || opt.optionText || opt.value }}
          </span>

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

    <div v-else-if="currentView === 'library'" class="max-w-6xl mx-auto px-4 py-8 w-full animate-fade-in">

      <div class="mb-10 text-center md:text-left flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 class="text-3xl md:text-5xl font-black text-slate-800 tracking-tight">The Knowledge Forge</h1>
          <p class="text-slate-500 mt-2 text-lg">Access study guides, architectural diagrams, and video modules.</p>
        </div>

        <button @click="currentView = 'landing'" class="text-sm font-bold text-slate-500 hover:text-slate-800 transition-colors flex items-center gap-2 justify-center">
          <span>← Back to Command Center</span>
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
        <div
            v-for="module in libraryModules"
            :key="module.id"
            @click="openModule(module.id)"
            class="bg-white border-2 border-slate-100 rounded-3xl p-6 md:p-8 cursor-pointer transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:border-blue-200 group flex flex-col justify-between h-full"
        >
          <div>
            <div class="flex items-start justify-between mb-4">
          <span class="text-4xl md:text-5xl bg-slate-50 p-4 rounded-2xl group-hover:scale-110 group-hover:bg-blue-50 transition-all duration-300">
            {{ module.icon }}
          </span>
              <span class="bg-slate-100 text-slate-600 text-xs font-black px-3 py-1 rounded-full uppercase tracking-widest">
            {{ module.lessonCount }} Modules
          </span>
            </div>

            <h2 class="text-2xl font-bold text-slate-800 mb-2 group-hover:text-blue-600 transition-colors">
              {{ module.title }}
            </h2>
            <p class="text-slate-500 font-medium leading-relaxed">
              {{ module.description }}
            </p>
          </div>

          <div class="mt-8 pt-6 border-t-2 border-slate-50">
            <div class="flex justify-between text-xs font-bold text-slate-400 mb-2">
              <span>MODULE PROGRESS</span>
              <span>{{ module.progress }}%</span>
            </div>
            <div class="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
              <div class="bg-blue-500 h-full rounded-full transition-all duration-1000" :style="`width: ${module.progress}%`"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentView === 'lessonViewer' && activeModule" class="max-w-4xl mx-auto px-4 py-8 w-full animate-fade-in">

      <button @click="currentView = 'library'" class="group text-sm font-bold text-slate-500 hover:text-blue-600 transition-colors flex items-center gap-2 mb-8">
        <span class="group-hover:-translate-x-1 transition-transform">←</span>
        <span>Back to Library</span>
      </button>

      <div class="mb-10">
        <div class="flex items-center gap-4 mb-4">
          <span class="text-4xl bg-slate-50 p-3 rounded-2xl">{{ activeModule.icon }}</span>
          <h1 class="text-3xl md:text-4xl font-black text-slate-800 tracking-tight">{{ activeModule.title }}</h1>
        </div>
        <p class="text-slate-500 text-lg border-b-2 border-slate-100 pb-8">{{ activeModule.description }}</p>
      </div>

      <div v-if="activeModule.videos && activeModule.videos.length > 0" class="mb-12">
        <div class="w-full aspect-video bg-slate-900 rounded-3xl shadow-xl overflow-hidden mb-6 relative">
          <iframe
              :src="`https://www.youtube.com/embed/${activeModule.videos[currentVideoIndex].id}?rel=0`"
              class="w-full h-full border-0 absolute inset-0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
          ></iframe>
        </div>

        <div v-if="activeModule.videos.length > 1" class="flex flex-col gap-3">
          <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest px-2">Module Playlist</h4>
          <div class="flex flex-wrap gap-3">
            <button
                v-for="(video, index) in activeModule.videos"
                :key="index"
                @click="currentVideoIndex = index"
                :class="currentVideoIndex === index ? 'bg-blue-600 text-white shadow-md border-blue-600' : 'bg-white text-slate-600 border-slate-200 hover:border-blue-300 hover:bg-blue-50 hover:shadow-sm'"
                class="px-5 py-3 rounded-xl border font-bold text-sm transition-all flex items-center gap-3 group"
            >
              <span v-if="currentVideoIndex === index" class="animate-pulse">▶</span>
              <span v-else class="opacity-50 font-mono text-xs">{{ index + 1 }}.</span>
              {{ video.title }}
            </button>
          </div>
        </div>
      </div>

      <div v-else class="w-full aspect-video bg-slate-900 rounded-3xl shadow-xl overflow-hidden mb-12 relative flex flex-col items-center justify-center text-slate-600">
        <span class="text-4xl mb-4 opacity-50">📡</span>
        <span class="font-bold tracking-widest text-sm uppercase opacity-50">
          Video Transmission Offline
        </span>
      </div>

      <div class="prose prose-slate prose-lg md:prose-xl max-w-none mb-16">

        <div v-html="activeModule.studyGuide"></div>

        <div class="bg-emerald-50 border-l-4 border-emerald-500 p-6 rounded-r-2xl mt-8 text-emerald-900 font-medium">
          💡 System Verified: This text was dynamically pulled from your Spring Boot Database!
        </div>

      </div>

      <div class="flex justify-center border-t-2 border-slate-100 pt-12">
        <button @click="completeActiveModule" class="bg-slate-800 hover:bg-black text-white px-8 py-4 rounded-2xl font-black uppercase tracking-widest text-sm transition-all hover:shadow-xl hover:-translate-y-1 flex items-center gap-3">
          <span class="text-emerald-400 text-xl">✓</span>
          Mark Module as Complete
        </button>
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
          </template>

          <input v-model="authForm.password" type="password" placeholder="Secret Access: Password" class="w-full p-4 rounded-2xl border border-slate-200 text-center font-bold focus:border-blue-500 outline-none auth-input" />

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

    <div v-if="showReviewModal" class="max-w-7xl mx-auto px-4 md:px-8 relative z-50">
      <div class="bg-white rounded-[2rem] shadow-2xl overflow-hidden border border-slate-100">

        <div class="bg-slate-50 p-10 border-b border-slate-200">
          <h1 class="text-4xl md:text-5xl font-black text-slate-950 tracking-tight">Exam Review</h1>
          <p class="text-slate-600 mt-1 uppercase text-xs font-bold tracking-widest">Analyzing Missed Concepts</p>
        </div>

        <div class="p-8 md:p-12 max-h-[75vh] overflow-y-auto space-y-12">
          <div v-for="(q, index) in reviewQuestions" :key="q.id" class="relative pl-12">

            <div class="flex justify-between items-center mb-6">
              <div class="flex items-center gap-3">
                <div class="absolute left-0 top-0 w-8 h-8 rounded-full bg-gradient-to-br from-blue-600 to-blue-500 text-white flex items-center justify-center font-black text-sm shadow-md">
                  {{ index + 1 }}
                </div>
                <h3 class="text-xl md:text-2xl font-extrabold text-slate-900 leading-tight">Question Details</h3>
              </div>
              <span
                  :class="userResults[q.id] ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'"
                  class="font-black uppercase text-xs tracking-wider px-3 py-1 rounded-full flex items-center gap-1.5"
              >
            {{ userResults[q.id] ? '✓ Correct' : '✗ Incorrect' }}
          </span>
            </div>

            <p class="text-slate-800 text-lg leading-relaxed mb-8">{{ q.questionText }}</p>

            <div class="bg-blue-50/50 p-6 md:p-8 rounded-3xl border border-blue-100 shadow-inner relative">
              <svg class="absolute -top-4 -left-4 w-10 h-10 text-blue-200 opacity-60" fill="currentColor" viewBox="0 0 24 24"><path d="M13 14.725c0-5.141 3.892-10.519 10-11.725l.943 1.956c-3.18 1.018-4.943 3.903-4.943 6.451 0 2.067 1.341 3.067 2.499 3.067 1.366 0 2.501 1.258 2.501 2.91 0 2.228-2.028 4.616-5.123 4.616-3.745 0-5.877-2.926-5.877-7.275zm-12 0c0-5.141 3.892-10.519 10-11.725l.943 1.956c-3.18 1.018-4.943 3.903-4.943 6.451 0 2.067 1.341 3.067 2.499 3.067 1.366 0 2.501 1.258 2.501 2.91 0 2.228-2.028 4.616-5.123 4.616-3.745 0-5.877-2.926-5.877-7.275z"/></svg>
              <p class="text-blue-600 text-[11px] font-black uppercase tracking-widest mb-1.5">Detailed Explanation</p>
              <p class="text-sm md:text-base italic leading-relaxed text-blue-900/90">{{ q.explanation }}</p>
            </div>

            <div class="h-px bg-slate-100 mt-12 w-full"></div>
          </div>
        </div>

        <div class="bg-slate-50 p-8 border-t border-slate-200 mt-4 rounded-b-[2rem]">
          <div class="flex flex-col md:flex-row justify-center items-center gap-4">

            <button
                @click="handlePrint"
                class="w-full md:w-auto flex items-center justify-center gap-2 bg-slate-800 hover:bg-slate-950 text-white py-4 px-10 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
            >
              <svg class="w-5 h-5 text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
              Export to PDF
            </button>

            <button
                @click="closeReview"
                class="w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white py-4 px-12 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
            >
              ✓ Got it! Close Review
            </button>

          </div>
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

    <div v-if="showGradeConfirmModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/80 backdrop-blur-md p-4 transition-all">
      <div class="relative bg-slate-950 rounded-2xl shadow-[0_0_50px_-12px_rgba(220,38,38,0.5)] max-w-lg w-full overflow-hidden border border-slate-800 transform scale-100 animate-[fadeIn_0.2s_ease-out]">

        <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-red-600 via-orange-500 to-red-600 animate-pulse"></div>

        <div class="p-6 md:p-8 flex items-start gap-5 border-b border-slate-800/60 bg-gradient-to-b from-red-950/20 to-transparent">
          <div class="w-14 h-14 rounded-xl bg-red-500/10 border border-red-500/20 flex items-center justify-center flex-shrink-0 relative overflow-hidden">
            <div class="absolute inset-0 bg-red-500/20 animate-ping opacity-50"></div>
            <span class="text-3xl relative z-10">⚠️</span>
          </div>
          <div>
            <h3 class="text-2xl font-black text-slate-100 tracking-tight uppercase">System Override</h3>
            <div class="flex items-center gap-2 mt-2 text-red-500 font-mono text-[10px] md:text-xs tracking-widest uppercase bg-red-500/10 px-2 py-1 rounded inline-flex">
              <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
              Warning: Premature Termination
            </div>
          </div>
        </div>

        <div class="p-6 md:p-8 font-mono text-sm">
          <div class="text-slate-400 mb-6 leading-relaxed space-y-2">
            <p>> Analyzing current session state...</p>
            <p>> <span class="text-amber-400">Warning: Unresolved parameters detected.</span></p>
            <p>> Halting the simulation now will result in all uncommitted queries being flagged as <span class="text-red-400 font-bold bg-red-500/10 px-1">CRITICAL_FAILURES</span>.</p>
          </div>

          <div class="bg-slate-900 border border-slate-800 p-4 rounded-lg">
            <p class="text-slate-300 font-bold tracking-wide">
              Execute final telemetry calculation anyway?
            </p>
          </div>
        </div>

        <div class="bg-slate-950 p-6 md:p-8 border-t border-slate-800 flex flex-col md:flex-row justify-end gap-4">
          <button
              @click="showGradeConfirmModal = false"
              class="px-6 py-3 rounded-xl font-mono text-sm font-bold text-slate-400 hover:text-white hover:bg-slate-800 transition-colors border border-transparent hover:border-slate-700"
          >
            [ ESC ] ABORT
          </button>
          <button
              @click="executeGradingSequence"
              class="group relative px-8 py-3 rounded-xl font-mono text-sm font-black text-red-100 bg-red-600 overflow-hidden shadow-[0_0_20px_rgba(220,38,38,0.4)] hover:shadow-[0_0_30px_rgba(220,38,38,0.6)] transition-all"
          >
            <div class="absolute inset-0 bg-white/20 translate-y-full group-hover:translate-y-0 transition-transform duration-300 ease-in-out"></div>
            <span class="relative z-10">> EXECUTE_GRADE</span>
          </button>
        </div>

      </div>
    </div>

  </div>
  </template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue';
import { jsPDF } from "jspdf";
import autoTable from 'jspdf-autotable';
import { Radar } from 'vue-chartjs';
import { Chart as ChartJS, RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend } from 'chart.js';

ChartJS.register(RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend);
// --- Core State ---
const question = ref(null);
const selectedAnswers = ref([]);
const feedback = ref('');
const feedbackClass = ref('');
const showExplanation = ref(false);
const currentQuestion = ref(null);
const questions = ref([]);
const reviewQuestions = ref([]);
const missedQuestionIds = ref([]); // Tracks IDs for the Review Room
const showGradeConfirmModal = ref(false);
const leaderboardData = ref([]);
const totalExamSeconds = ref(0);
const timerSeconds = ref(0);
const currentQuestionSeconds = ref(0);
let timerInterval = null;
const userTotalXp = ref(0);
const isSuddenDeath = ref(true);
const healthPoints = ref(3);
const isDead = computed(() => healthPoints.value <= 0);
// --- AMRAP (TIME ATTACK) MECHANIC ---
const isAmrapMode = ref(true); // Toggle ON for testing!
const amrapTimeLeft = ref(600); // 10 minutes (in seconds)
const amrapCorrectCount = ref(0);
let amrapInterval = null;
const activeProtocol = ref('standard');

// --- Exam & Session Logic ---
const sessionCount = ref(1);
const totalExamQuestions = ref(9);
const seenQuestionIds = ref([]);
const allQuestionsInSession = ref([]); // Added this to prevent "undefined" errors
const selectedAnswersRecord = ref({}); // Added this to prevent "undefined" errors
const MAX_HISTORY = 30;
const userStreak = ref(0);
const activeModule = ref(null);
const currentVideoIndex = ref(0);

// --- Performance Tracking ---
const showResults = ref(false);
const isHistoricalView = ref(false);
const startTime = ref(null);
const totalExamTime = ref(0);
const savedScores = localStorage.getItem('aws_radar_scores');
const categoryScores = ref(savedScores ? JSON.parse(savedScores) : {});

// --- Platform State ---
const currentView = ref('landing');
const selectedCert = ref(null);
const showGuestLimitModal = ref(false);
const hallOfFame = ref([]);

// --- Timer Logic ---
const timer = ref(0);

// JWT AUTHENTICATION ENGINE
const showAuthModal = ref(false);
const isLoginMode = ref(true); // Toggle between Login and Register
const authForm = ref({ username: '', email: '', password: '' });
const authError = ref('');
const authToken = ref(localStorage.getItem('aws_jwt') || null); // Load saved token if it exists

// --- Category Logic ---
const selectedCategory = ref('All');
const categories = ['All', 'Data Engineering', 'Algorithms', 'Modeling', 'Deployment', 'Security', 'AI Services'];

// --- Exam History ---
const examHistory = ref([]);

// --- User Login ---
const currentUser = ref(null);
const isSignup = ref(true);
const userResults = ref({});

const isAudioEnabled = ref(true); // Global mute switch
const isMusicPlaying = ref(false); // Background lo-fi track

// --- Review & Print ---
const showReviewModal = ref(false);

const allExamQuestions = ref([]);

// --- The functions start here

const showSuccessHologram = ref(false);
const welcomeMessage = ref('');
const lastAttemptId = ref(null);

// --- THE KNOWLEDGE FORGE: DYNAMIC DATABASE FETCH ---
const libraryModules = ref([]); // Starts completely empty!

// --- DYNAMIC TARGETED FETCH ---
const fetchKnowledgeForge = async (targetCertCode) => {
  if (!targetCertCode) return;

  try {
    // 1. Fetch the courses for this specific cert
    const response = await fetch(`http://localhost:8080/api/modules/cert/${targetCertCode}`);

    // 2. Fetch the logged-in user's progress history
    let completedModules = [];
    if (currentUser.value && currentUser.value.username) {
      const progressResponse = await fetch(`http://localhost:8080/api/progress/${currentUser.value.username}`);
      if (progressResponse.ok) {
        completedModules = await progressResponse.json();
      }
    }

    if (response.ok) {
      const dbModules = await response.json();

      libraryModules.value = dbModules.map(mod => ({
        id: mod.id,
        title: mod.title,
        icon: mod.icon,
        description: mod.description,
        lessonCount: mod.videoIds ? mod.videoIds.length : 0,
        // THE MAGIC: If the database says they completed it, fill the bar to 100%!
        progress: completedModules.includes(mod.id) ? 100 : 0,
        studyGuide: mod.studyGuideContent,
        videos: mod.videoIds ? mod.videoIds.map((vidId, i) => ({ title: `Video Lesson ${i + 1}`, id: vidId })) : []
      }));
    }
  } catch (error) {
    console.error("The Jedi Archives are offline.", error);
  }
};

// --- ENTER THE FORGE ---
// Update your existing enterKnowledgeForge function to fetch the data BEFORE changing the screen!
const enterKnowledgeForge = async () => {
  // Pass the user's currently selected certification to the fetch engine
  await fetchKnowledgeForge(selectedCert.value.code);
  currentView.value = 'library';
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// --- Analytics: Radar Chart Configuration ---
// 1. The Official AWS Domain Dictionary
// (You can add the exact domains for all 12 certs here later!)
const chartViewCode = ref('DEFAULT');

// The Official AWS Domain Dictionary (Formatted for Radar Charts)
const examDomains = {
  // --- FOUNDATIONAL ---
  'CLF-C02': ['Cloud Concepts', 'Security & Compliance', 'Tech & Services', 'Billing & Pricing'],
  'AIF-C01': ['AI/ML Fundamentals', 'Generative AI', 'Foundation Models', 'Responsible AI', 'Security'],

  // --- ASSOCIATE ---
  'SAA-C03': ['Secure Architectures', 'Resilient Architectures', 'High-Performing', 'Cost-Optimized'],
  'DVA-C02': ['Development', 'Security', 'Deployment', 'Troubleshooting & Optimization'],
  'SOA-C02': ['Monitoring & Logging', 'Reliability', 'Deployment & Automation', 'Security', 'Networking', 'Optimization'],
  'DEA-C01': ['Ingestion & Transform', 'Data Stores', 'Operations & Support', 'Security & Governance'],

  // --- PROFESSIONAL ---
  'SAP-C02': ['Organizational Complexity', 'New Solutions', 'Continuous Improvement', 'Migration & Modernization'],
  'DOP-C02': ['SDLC Automation', 'IaC & Config', 'Resiliency', 'Monitoring', 'Incident Response', 'Security'],
  'AIP-C01': ['GenAI Fundamentals', 'LLM Integration', 'Prompt Engineering', 'Security & Ethics'], // Generative AI Dev

  // --- SPECIALTY ---
  'MLS-C01': ['Data Engineering', 'Exploratory Data Analysis', 'Modeling', 'ML Implementation & Ops'],
  'SCS-C02': ['Threat Detection', 'Logging & Monitoring', 'Infrastructure Security', 'IAM', 'Data Protection'],
  'ANS-C01': ['Network Design', 'Implementation', 'Management & Ops', 'Network Security'],

  // --- DEFAULT ---
  'DEFAULT': ['Compute', 'Storage', 'Database', 'Networking', 'Security', 'Management']
};

// 2. The Dynamic Radar Engine
const radarData = computed(() => {
  // Figure out which cert they are looking at, or use the default
  const certCode = chartViewCode.value;

  // Grab the specific labels for that exact exam
  const currentLabels = examDomains[certCode] || examDomains['DEFAULT'];

  const realScores = currentLabels.map(label => {
    // If we have a real score for this category, use it!
    if (categoryScores.value && categoryScores.value[label] !== undefined) {
      return categoryScores.value[label];
    }
    // If we haven't tested this category yet, default to 0
    return 0;
  });

  // Generate placeholder scores that perfectly match the number of labels
  // (We randomize them between 40-100 just so the chart looks cool for testing)
  const mockScores = currentLabels.map(() => Math.floor(Math.random() * 60) + 40);

  return {
    labels: currentLabels,
    datasets: [
      {
        label: `${certCode === 'DEFAULT' ? 'Overall AWS' : certCode} Mastery`,
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        borderColor: 'rgba(59, 130, 246, 1)',
        pointBackgroundColor: 'rgba(59, 130, 246, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(59, 130, 246, 1)',
        data: realScores // <-- Feeding the real scores into the chart!
      }
    ]
  };
});

// --- Analytics: Study Priorities Engine ---
// --- THE REACTIVE PRIORITIES ENGINE ---
const studyPriorities = computed(() => {
  // 1. Safety check: if no data exists yet, return an empty array
  if (!categoryScores.value || Object.keys(categoryScores.value).length === 0) {
    return [];
  }

  // 2. Map over the scores to format them for the UI
  const priorities = Object.entries(categoryScores.value).map(([cat, score]) => {
    return {
      name: cat,
      percentage: score // No math here, just passing the number through!
    };
  });

  // 3. Sort from lowest to highest, grab the bottom 3, and send them to the screen!
  return priorities.sort((a, b) => a.percentage - b.percentage).slice(0, 3);
});

const radarOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    r: {
      // THE SPOKES (The lines coming out from the center)
      angleLines: {
        color: 'rgba(0, 0, 0, 5)',
        lineWidth: 1 // <-- Make the spokes thicker or thinner
      },

      // THE WEB (The concentric polygon rings)
      grid: {
        color: 'rgba(0, 0, 0, 5)',
        circular: true, // <-- Change to 'true' for a circle instead of a polygon!
        lineWidth: 1
      },

      // THE FONTS (The category names around the outside)
      pointLabels: {
        font: {
          size: 13, // <-- Change font size
          family: "'Inter', 'Helvetica Neue', sans-serif", // <-- Change font family
          weight: '800' // <-- Make it bolder
        },
        color: '#334155' // <-- Change the text color (Tailwind slate-700)
      },

      // THE RINGS (Locking the scale from 0 to 100)
      ticks: {
        display: false, // <-- Change to 'true' to see the numbers (20, 40, 60, 80) on the rings
        min: 0,
        max: 100,
        stepSize: 20 // <-- Creates exactly 5 concentric rings
      }
    }
  },
  plugins: { legend: { display: false } }
};

const printReview = () => {
  window.print();
};

const openReviewRoom = async (attemptId) => {
  // 1. THE SMARTER LOCAL BYPASS (For immediately after an exam ends)
  const localData = allQuestionsInSession.value.length > 0 ? allQuestionsInSession.value : questions.value;

  // If we have active questions AND we are currently on the Results screen, use the RAM!
  if (localData && localData.length > 0 && currentView.value === 'quiz') {
    reviewQuestions.value = localData;
    showResults.value = false;
    showReviewModal.value = true;
    return;
  }

  // 2. THE HISTORICAL FETCH (For clicking the 👁️ on the dashboard)
  // Check if attemptId is missing or if it's accidentally a mouse click event object
  if (!attemptId || typeof attemptId === 'object') {
    alert("Unable to load review. Invalid attempt ID.");
    return;
  }

  // --- THE FIX: Wrap both IDs in String() so "15" === "15" works perfectly! ---
  const targetAttempt = examHistory.value.find(a => String(a.id) === String(attemptId));

  if (!targetAttempt) {
    alert("Could not find this attempt in your history archives.");
    return;
  }

  // Handle Flawless Victories (No missed questions!)
  if (!targetAttempt.missedQuestionIds || targetAttempt.missedQuestionIds.trim() === "") {
    alert("Flawless Victory! You didn't miss any questions on this attempt.");
    return;
  }

  // Fetch the specific missed questions from Spring Boot
  try {
    const response = await fetch(`http://localhost:8080/api/questions/batch?ids=${targetAttempt.missedQuestionIds}`);

    if (!response.ok) throw new Error("Backend refused the batch request.");

    const missedQuestionsData = await response.json();

    // 1. Load the questions into Vue's memory
    reviewQuestions.value = missedQuestionsData;

    // 2. Force them to 'false' so the UI highlights them as missed
    userResults.value = {};
    missedQuestionsData.forEach(q => {
      userResults.value[q.id] = false;
    });

    // 3. THE FIX: Explicitly change the Vue state so the HTML actually renders!
    currentView.value = 'quiz';      // Move off the dashboard!
    showResults.value = true;        // Trigger the results screen!
    isHistoricalView.value = true;

    // (If you have a separate boolean for a modal, keep it here just in case)
    if (typeof showReviewModal !== 'undefined') {
      showReviewModal.value = true;
    }

  } catch (error) {
    console.error("Historical Review Error:", error);
    alert("The Jedi Archives are offline. Could not retrieve past questions.");
  }
};

const resetSession = () => {
  // 1. Aggressively wipe all tracking data arrays/objects clean!
  sessionCount.value = 1; // Start at 1, not 0!
  questions.value = [];
  allQuestionsInSession.value = [];
  reviewQuestions.value = [];
  userResults.value = {};
  selectedAnswersRecord.value = {};
  selectedAnswers.value = [];
  missedQuestionIds.value = [];
  lastAttemptId.value = null;

  // 2. Turn off all the end-of-quiz UI screens
  showResults.value = false;
  showReviewModal.value = false;
  showExplanation.value = false;
  feedback.value = '';

  // 3. Route back to the landing dashboard!
  currentView.value = 'landing';

  // 4. Scroll to the top of the page
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const scorePercentage = computed(() => {
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
  showResults.value = false;
  isHistoricalView.value = false;
  currentView.value = 'landing';

  if (typeof fetchHistory === 'function') fetchHistory();
  if (typeof fetchLeaderboard === 'function') fetchLeaderboard();
};

const isCorrect = (q, userPick) => {
  const correctIds = q.options
      .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
      .map(opt => String(opt.id));
  const pickedIds = Array.isArray(userPick) ? userPick.map(id => String(id)) : [String(userPick)];
  return pickedIds.length === correctIds.length && pickedIds.every(id => correctIds.includes(id));
};

const startTimer = () => {
  stopTimer(); // Safety check to prevent double-speed clocks!
  timerInterval = setInterval(() => {
    totalExamSeconds.value++;
    currentQuestionSeconds.value++;
  }, 1000);
};


const stopTimer = () => {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
};

watch(question, () => {
  currentQuestionSeconds.value = 0;
  autoSaveSession();
});

const isUserCorrect = (questionId) => {
  return userResults.value[questionId] === true;
};

const formattedTime = computed(() => {
  // Accessing .value here is what tells Vue to update the HTML
  const totalSeconds = timerSeconds.value;
  const mins = Math.floor(totalSeconds / 60);
  const secs = totalSeconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
});


// Function to handle clicking a card
const openModule = (moduleId) => {
  activeModule.value = libraryModules.value.find(m => m.id === moduleId);
  currentVideoIndex.value = 0; // <-- Resets playlist to the first video
  currentView.value = 'lessonViewer';
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// --- COMPLETE MODULE PROGRESSION ---
const completeActiveModule = async () => {
  // 1. Security Check: Are they logged in?
  if (!currentUser.value || !currentUser.value.username) {
    alert("⚠️ Authentication Required: Please log in to save your progress!");
    return;
  }

  try {
    // 2. Send the completion data to Spring Boot
    // Inside your completeActiveModule function...
    const response = await fetch(`http://localhost:8080/api/progress/complete?username=${currentUser.value.username}&moduleId=${activeModule.value.id}`, {
      method: 'POST',
      headers: {
        // THIS IS THE VIP PASS!
        'Authorization': `Bearer ${authToken.value}`
      }
    });

    if (response.ok) {
      // 3. Update the local UI (Fill the bar instantly!)
      activeModule.value.progress = 100;

      // 4. Award 500 XP and save it locally
      userTotalXp.value += 500;
      currentUser.value.xp = userTotalXp.value;
      localStorage.setItem('aws_user', JSON.stringify(currentUser.value)); // Sync to permanent browser storage

      // 5. Send them back to the grid with a victory message
      alert(`✅ System Mastery Achieved!\n+500 XP awarded to ${currentUser.value.username}.`);
      currentView.value = 'library';
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  } catch (error) {
    console.error("Transmission failed:", error);
    alert("❌ Error connecting to the Jedi Archives.");
  }
};

// --- THE SMART-LINK TELEPORTER ---
const jumpToForge = async (categoryName) => {
  if (!categoryName) return;

  try {
    let targetCode = selectedCert.value?.code;

    // Tiny helper function: Strips ALL spaces, commas, and special characters
    const normalize = (str) => str.toLowerCase().replace(/[^a-z0-9]/g, '');
    const normCategory = normalize(categoryName);

    // 1. THE FALLBACK: Auto-select cert if clicked from global dashboard
    if (!targetCode) {
      const response = await fetch('http://localhost:8080/api/modules');
      if (response.ok) {
        const allModules = await response.json();

        // Use Fuzzy Mathing
        const foundModule = allModules.find(m => {
          const normTitle = normalize(m.title);
          return normTitle.includes(normCategory) || normCategory.includes(normTitle);
        });

        if (foundModule) {
          targetCode = foundModule.certCode;
          for (const category in groupedCerts.value) {
            const match = groupedCerts.value[category].find(c => c.code === targetCode);
            if (match) {
              selectedCert.value = match;
              break;
            }
          }
        }
      }
    }

    if (!targetCode) {
      alert(`⚠️ Could not locate the Jedi Archives for "${categoryName}". Please select a certification path above first.`);
      return;
    }

    // 2. FORCE FETCH
    await fetchKnowledgeForge(targetCode);

    // 3. THE FUZZY SEARCH (The Magic Fix)
    const targetModule = libraryModules.value.find(m => {
      const normTitle = normalize(m.title);
      // Now it successfully matches "Security, Compliance..." with "Security Compliance..."
      return normTitle.includes(normCategory) || normCategory.includes(normTitle);
    });

    // 4. EXECUTE JUMP
    if (targetModule) {
      activeModule.value = targetModule;
      currentVideoIndex.value = 0;
      currentView.value = 'lessonViewer';
    } else {
      console.warn(`Target Missed: Teleporter could not match "${categoryName}" to any database module.`);
      currentView.value = 'library'; // Fallback to Grid
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });

  } catch (error) {
    console.error("Teleporter malfunctioned:", error);
  }
};

// 1. The clock shown during the quiz (resets every question)
const formattedCurrentTime = computed(() => {
  const m = Math.floor(currentQuestionSeconds.value / 60).toString().padStart(2, '0');
  const s = (currentQuestionSeconds.value % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// 2. The total time shown on the Results screen
const formattedTotalTime = computed(() => {
  const m = Math.floor(totalExamSeconds.value / 60).toString().padStart(2, '0');
  const s = (totalExamSeconds.value % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// 3. The average time per question shown on the Results screen
const formattedAverageTime = computed(() => {
  // Figure out how many questions were in this specific exam
  const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  const totalQs = activeQs.length || 1;

  // Calculate average
  const avgSeconds = Math.round(totalExamSeconds.value / totalQs);

  const m = Math.floor(avgSeconds / 60).toString().padStart(2, '0');
  const s = (avgSeconds % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// The Marvel Cinematic Universe message engine
const examResultMessage = computed(() => {
  if (finalScore.value >= 72) {
    return {
      title: "You are Worthy. ⚡️",
      text: "The cloud bends to your will. You've summoned the lightning, conquered the architecture, and secured your place among the AWS Pantheon. You are officially God of Thunder tier.",
      color: "text-emerald-500",
      icon: "🌩️",
      image: "/image/thor.gif" // Thor ascending
    };
  } else {
    return {
      title: "Whatever It Takes. 🛡️",
      text: "Even Earth's mightiest heroes take a hit. Dust yourself off, review the mission logs in the archives below, and gear up for round two. We don't give up. Get back in the fight.",
      color: "text-blue-500",
      icon: "💥",
      image: "/image/marvel.gif" // Avengers standing tall
    };
  }
});

const fetchQuestion = async () => {
  if (!selectedCert.value?.code) return;

  try {
    // 1. Build the filter string (e.g., "&excludeIds=4,12,88")
    const excludeQuery = seenQuestionIds.value.length > 0
        ? `&excludeIds=${seenQuestionIds.value.join(',')}`
        : '';

    // 2. Ask the server for a random question, excluding the ones we've seen
    const response = await fetch(`http://localhost:8080/api/questions/random?examCode=${selectedCert.value.code}${excludeQuery}`);

    // 3. If the server returns 204, the bank is completely empty!
    if (response.status === 204 || !response.ok) {
      alert("Simulation Complete: You have exhausted the entire question bank for this certification!");

      // If we are in the middle of a quiz and run out of questions, force the grade!
      if (currentView.value === 'quiz') forceGradeExam();
      return;
    }

    const text = await response.text();
    if (!text) return;

    const data = JSON.parse(text);
    question.value = data;

    // 4. THE FIX: Log this ID into our memory bank so it never shows up again!
    seenQuestionIds.value.push(data.id);

    // Reset state for the new question
    selectedAnswers.value = [];
    showExplanation.value = false;

  } catch (error) {
    console.error(error);
    alert("Communication error with the Jedi Archives.");
  }
};

const loadNextQuestion = async () => {
  if (question.value) {
    questions.value.push(question.value);
  }

  // --- THE GUEST TRIPWIRE (Guests still get kicked out at 10) ---
  if (!currentUser.value && sessionCount.value >= 10) {
    if (timerInterval) clearInterval(timerInterval);
    if (amrapInterval) clearInterval(amrapInterval);
    showGuestLimitModal.value = true;
    return;
  }

  if (isAmrapMode.value) {
    // --- AMRAP INFINITE LOOP ---
    try {
      const codeToFetch = selectedCert.value?.code || 'SCS-C02';

      // 1. THE FIX: Build the Memory Filter string for AMRAP!
      const excludeQuery = seenQuestionIds.value.length > 0
          ? `&excludeIds=${seenQuestionIds.value.join(',')}`
          : '';

      // 2. Fetch using the filter
      const response = await fetch(`http://localhost:8080/api/questions/random?examCode=${codeToFetch}${excludeQuery}`);

      // 3. THE SAFETY NET: Check if they are an absolute beast and exhausted the bank!
      if (response.status === 204 || !response.ok) {
        alert("Bank Exhausted! You have ripped through every single question available for this certification.");
        forceGradeExam(); // End the AMRAP session early and score them!
        return;
      }

      const text = await response.text();
      if (!text) return;
      const newQuestion = JSON.parse(text);

      // 4. Log this new question in the memory bank so it doesn't repeat
      seenQuestionIds.value.push(newQuestion.id);

      question.value = newQuestion;
      selectedAnswers.value = [];
      showExplanation.value = false;
      sessionCount.value++; // Keep track of total reps attempted

      window.scrollTo({top: 0, behavior: 'smooth'});

    } catch (error) {
      console.error("Failed to fetch AMRAP question:", error);
    }
  } else {
    // --- STANDARD EXAM ENGINE ---

    // THE FINISH LINE TRIPWIRE (Only applies to normal exams!)
    if (sessionCount.value >= totalExamQuestions.value) {
      finishExam();
      return;
    }

    // THE MEMORY WIPE
    showExplanation.value = false;
    selectedAnswers.value = [];
    feedback.value = '';

    // INCREMENT & FETCH
    sessionCount.value++;

    try {
      // Your fetchQuestion function handles the memory filter perfectly for standard mode!
      await fetchQuestion();
      window.scrollTo({top: 0, behavior: 'smooth'});
    } catch (error) {
      alert("Could not load the next trial. Please check your uplink.");
    }
  }
};

const checkAnswer = () => {
  const currentQ = question.value;

  if (!currentQ || !currentQ.options) {
    return;
  }

  // Save their answer
  selectedAnswersRecord.value[currentQ.id] = [...selectedAnswers.value];

  // --- THE MULTI-SELECT UPGRADE ---
  const userSelectedIds = selectedAnswers.value.map(id => String(id));
  const correctOptionIds = currentQ.options
      .filter(opt => opt.isCorrect === true || opt.correct === true)
      .map(opt => String(opt.id));

  const isRight =
      userSelectedIds.length === correctOptionIds.length &&
      userSelectedIds.every(id => correctOptionIds.includes(id));
  // ---------------------------------

  userResults.value[currentQ.id] = isRight;

  // Set visual feedback for the explanation screen
  if (isRight) {
    playSound('correct');
    feedback.value = "✓ CORRECT";
    feedbackClass.value = "bg-green-100 text-green-800 border-2 border-green-200";
  } else {
    playSound('wrong');
    feedback.value = "✗ INCORRECT";
    feedbackClass.value = "bg-red-100 text-red-800 border-2 border-red-200";
  }

  // --- THE SUDDEN DEATH ENGINE ---
  if (isSuddenDeath.value) {
    if (!isRight) {
      playSound('shatter');
      healthPoints.value--;
      console.warn(`DAMAGE TAKEN! HP remaining: ${healthPoints.value}`);

      // Fatal Blow - Terminate the Exam instantly
      if (isDead.value) {
        // Instantly force the grade and boot them to the results screen
        forceGradeExam();
        return;
      }
    }
  }

  // Show the explanation if they survived!
  showExplanation.value = true;
};

// The Countdown Formatter (e.g., "09:42")
const formattedAmrapTime = computed(() => {
  const m = Math.floor(amrapTimeLeft.value / 60);
  const s = amrapTimeLeft.value % 60;
  return `${m}:${s.toString().padStart(2, '0')}`;
});

const sfx = {
  click: new Audio('/sounds/mechanical_click.mp3'),
  correct: new Audio('/sounds/heavy_thud.mp3'),     // Or a satisfying chime
  wrong: new Audio('/sounds/error_glitch.mp3'),     // A digital glitch or low buzz
  shatter: new Audio('/sounds/glass_shatter.mp3'),  // For Sudden Death damage
  trash: new Audio('/sounds/digital_sweep.mp3')
};

// Lower the volume on the SFX so they aren't deafening
Object.values(sfx).forEach(audio => audio.volume = 0.4);

// 2. The Lo-Fi Focus Track
const bgMusic = new Audio('/sounds/lofi_focus_loop.mp3');
bgMusic.loop = true;
bgMusic.volume = 0.05; // Keep this very quiet so it sits in the background!

// 3. The Play Helper Function
const playSound = (type) => {
  if (!isAudioEnabled.value) return;

  // Clone the audio node so it can play rapidly overlapping clicks!
  const sound = sfx[type].cloneNode();
  sound.volume = sfx[type].volume;
  sound.play().catch(e => console.warn("Browser blocked audio auto-play"));
};

const toggleMusic = () => {
  isMusicPlaying.value = !isMusicPlaying.value;
  if (isMusicPlaying.value) {
    bgMusic.play().catch(e => console.warn("Browser blocked audio"));
  } else {
    bgMusic.pause();
  }
};

// The Strict 10-Minute Clock
const startAmrapTimer = () => {
  amrapTimeLeft.value = 600;
  amrapCorrectCount.value = 0;

  amrapInterval = setInterval(() => {
    amrapTimeLeft.value--;

    // THE BUZZER!
    if (amrapTimeLeft.value <= 0) {
      clearInterval(amrapInterval);
      console.warn("AMRAP CLOCK EXHAUSTED! Forcing submission...");

      examResultMessage.value = {
        title: "TIME EXHAUSTED",
        text: `AMRAP Session Complete. You successfully completed ${amrapCorrectCount.value} reps.`,
        color: "text-amber-500",
        icon: "⏱️"
      };

      forceGradeExam();
    }
  }, 1000);
};

const autoSaveSession = () => {
    if (currentView.value === 'quiz' && !showResults.value && question.value) {
    const sessionState = {
      certCode: selectedCert.value.code,
      mode: activeProtocol.value,
      certName: selectedCert.value.name,
      // Change these variable names to match whatever you named them in your code!
      sessionCount: sessionCount.value,
      healthPoints: healthPoints.value,
      savedQuestion: question.value
    };

    // Write it permanently to the browser's hard drive
    localStorage.setItem('aws_active_exam', JSON.stringify(sessionState));
  }
};

const analyzeCategoryMastery = () => {
  // 1. Get the active questions
  const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  // 2. Set up temporary counters
  const rawStats = {};

  // 3. Loop through every question in the exam
  activeQs.forEach(q => {
    const cat = q.category;

    // If we haven't seen this category yet, initialize it
    if (!rawStats[cat]) {
      rawStats[cat] = { total: 0, correct: 0 };
    }

    // Add to the total
    rawStats[cat].total++;

    // If they got it right, add to the correct count
    if (userResults.value[q.id] === true) {
      rawStats[cat].correct++;
    }
  });

  // 4. Convert the raw fractions into beautiful percentages
  const finalPercentages = {};
  for (const [cat, data] of Object.entries(rawStats)) {
    finalPercentages[cat] = Math.round((data.correct / data.total) * 100);
  }

  // 5. Save the real data to our Vue state!
  categoryScores.value = finalPercentages;

  // 6: Save to LocalStorage so it survives page reloads!
  localStorage.setItem('aws_radar_scores', JSON.stringify(finalPercentages));

  console.log("Mastery Calculated & Saved to Disk:", categoryScores.value);
};

const submitExam = async () => {
  stopTimer();
  // 1. Close the "Are you sure?" modal
  showGradeConfirmModal.value = false;

  // 2. Flip the screen to show the grading confetti and missed questions
  showResults.value = true;

  if (currentUser.value) {
    await saveResults();
  } else {
    console.warn("Ghost user detected! No one is logged in to save this score.");
  }
};

const saveResults = async () => {
  analyzeCategoryMastery();

  try {
    const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
        ? allQuestionsInSession.value
        : questions.value;

    const total = activeQs.length || 1;
    let correctCount = 0;
    let missedIds = [];

    // THE FIX: Loop through EVERY question in the exam.
    // If you didn't explicitly get it right, it is forced to "missed".
    activeQs.forEach(q => {
      if (userResults.value[q.id] === true) {
        correctCount++;
      } else {
        missedIds.push(q.id);
        userResults.value[q.id] = false; // Force the UI to show it as missed too!
      }
    });

    const finalScore = Math.round((correctCount / total) * 100);

    // Build the payload
    // Build the payload inside saveResults
    const historyData = {
      userId: currentUser.value.id,
      examCode: selectedCert.value?.code || 'AWS-CERT',
      scorePercentage: finalScore,
      totalQuestions: total,
      correctCount: correctCount,
      missedQuestionIds: missedIds.join(','),

      // CHANGE THIS FROM 0 to totalExamSeconds.value
      timeSpentSeconds: totalExamSeconds.value
    };

    // Make sure this URL matches your actual Spring Boot endpoint!
    const response = await fetch('http://localhost:8080/api/questions/history/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(historyData)
    });

  } catch (error) {
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

const streakDisplay = computed(() => {
  if (userStreak.value >= 14) {
    return { color: 'text-slate-900', icon: '🔥', label: 'Domain Expansion', aura: 'drop-shadow-[0_0_8px_rgba(0,0,0,0.8)]' }; // Black Flame
  } else if (userStreak.value >= 7) {
    return { color: 'text-blue-500', icon: '🔥', label: 'Blue Hot', aura: 'drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]' }; // Blue Flame
  } else if (userStreak.value >= 3) {
    return { color: 'text-orange-500', icon: '🔥', label: 'Ignited', aura: 'drop-shadow-[0_0_8px_rgba(249,115,22,0.8)]' }; // Orange Flame
  } else if (userStreak.value > 0) {
    return { color: 'text-amber-400', icon: '🔥', label: 'Spark', aura: '' }; // Standard Flame
  } else {
    return { color: 'text-slate-300', icon: '🧊', label: 'Cold', aura: '' }; // Frozen/Zero
  }
});

// --- THE PROTOCOL ROUTER ---
const initiateProtocol = async (mode) => {

  const savedSession = localStorage.getItem('aws_active_exam');

  if (savedSession) {
    const parsedSession = JSON.parse(savedSession);

    // Only ask to resume if they clicked the same cert they were previously studying!
    if (parsedSession.certCode === selectedCert.value.code) {
      const wantsToResume = confirm(`⚠️ Incomplete Exam Detected!\n\nDo you want to resume your ${parsedSession.certCode} exam at Question ${parsedSession.sessionCount}?\n\nClick 'Cancel' to delete the save and start fresh.`);

      if (wantsToResume) {
        // --- RESTORE THE SAVED STATE ---
        activeProtocol.value = parsedSession.mode;
        sessionCount.value = parsedSession.sessionCount;
        healthPoints.value = parsedSession.healthPoints;
        question.value = parsedSession.savedQuestion; // Reloads the exact question they were on!

        // Restore specific mode settings
        if (activeProtocol.value === 'amrap') {
          isAmrapMode.value = true;
          isSuddenDeath.value = false;
          startAmrapTimer(); // You might need to adjust this to pass saved time if you want!
        } else if (activeProtocol.value === 'sudden_death') {
          isAmrapMode.value = false;
          isSuddenDeath.value = true;
          startTimer();
        } else {
          isAmrapMode.value = false;
          isSuddenDeath.value = false;
          startTimer();
        }

        currentView.value = 'quiz';
        window.scrollTo({ top: 0, behavior: 'smooth' });
        return; // EXIT EARLY! This prevents the memory wipe below from running!
      } else {
        // They hit Cancel. Wipe the old save from the hard drive and start fresh.
        localStorage.removeItem('aws_active_exam');
      }
    }
  }

   activeProtocol.value = mode;

  // --- THE MEMORY WIPE ---
  seenQuestionIds.value = [];
  questions.value = [];
  allQuestionsInSession.value = [];
  reviewQuestions.value = [];
  userResults.value = {};
  selectedAnswersRecord.value = {};
  sessionCount.value = 1;
  showResults.value = false;
  isPaused.value = false;
  showExplanation.value = false;
  selectedAnswers.value = [];
  totalExamSeconds.value = 0;
  currentQuestionSeconds.value = 0;

  // --- CONFIGURE THE MODE ---
  if (mode === 'amrap') {
    isAmrapMode.value = true;
    isSuddenDeath.value = false;
    startAmrapTimer();
  }
  else if (mode === 'sudden_death') {
    isAmrapMode.value = false;
    isSuddenDeath.value = true;
    healthPoints.value = 3;
    startTimer();
  }
  else if (mode === 'learn') {
    isAmrapMode.value = false;
    isSuddenDeath.value = false;
    // No timer for learn mode!
  }
  else {
    // Standard Simulated Exam
    isAmrapMode.value = false;
    isSuddenDeath.value = false;
    startTimer();
  }

  // --- FETCH THE QUESTIONS ---
  try {
    if (mode === 'amrap') {
      const response = await fetch(`http://localhost:8080/api/questions/random?examCode=${selectedCert.value.code}`);

      if (response.status === 204 || !response.ok) {
        alert("Archive Empty: No questions found for this certification yet.");
        return;
      }

      const text = await response.text();
      if (!text) return;

      const newQuestion = JSON.parse(text);
      question.value = newQuestion;
      questions.value = [newQuestion];

    } else {
      await fetchQuestion();
    }

    // --- THE ULTIMATE SAFETY NET ---
    if (question.value) {
      currentView.value = 'quiz';
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      currentView.value = 'landing';
    }

  } catch (error) {
    console.error("Failed to initiate protocol:", error);
    alert("Could not load the simulation. Check your connection.");
  }
};

// 1. Add the Certifications list
const certifications = ref([
  // --- FOUNDATIONAL ---
  {
    code: 'CLF-C02', name: 'Cloud Practitioner', category: 'Foundational',
    image: '/badges/clf-badge.png', color: 'bg-orange-400', icon: '☁️'
  },
  {
    code: 'AIF-C01', name: 'AI Practitioner', category: 'Foundational',
    image: '/badges/aif-badge.png', color: 'bg-zinc-500', icon: '✨'
  },

  // --- ASSOCIATE ---
  {
    code: 'SAA-C03', name: 'Solutions Architect', category: 'Associate',
    image: '/badges/saa-badge.png', color: 'bg-blue-600', icon: '🏗️'
  },
  {
    code: 'DVA-C02', name: 'Developer Associate', category: 'Associate',
    image: '/badges/dva-badge.png', color: 'bg-blue-500', icon: '💻'
  },
  {
    code: 'SOA-C02', name: 'SysOps Admin', category: 'Associate',
    image: '/badges/soa-badge.png', color: 'bg-blue-400', icon: '⚙️'
  },
  {
    code: 'DEA-C01', name: 'Data Engineering', category: 'Associate',
    image: '/badges/dea-badge.png', color: 'bg-teal-500', icon: '🗄️'
  },

  // --- PROFESSIONAL ---
  {
    code: 'AIP-C01', name: 'Generative AI Developer', category: 'Professional',
    image: '/badges/aip-badge.png', color: 'bg-cyan-500', icon: '🧠'
  },
  {
    code: 'SAP-C02', name: 'Solutions Architect Pro', category: 'Professional',
    image: '/badges/sap-badge.png', color: 'bg-indigo-700', icon: '🏛️'
  },
  {
    code: 'DOP-C02', name: 'DevOps Engineer Pro', category: 'Professional',
    image: '/badges/dop-badge.png', color: 'bg-indigo-600', icon: '♾️'
  },

  // --- SPECIALTY ---
  {
    code: 'MLS-C01', name: 'Machine Learning', category: 'Specialty',
    image: '/badges/mls-badge.png', color: 'bg-purple-600', icon: '🤖'
  },
  {
    code: 'SCS-C02', name: 'Security Specialty', category: 'Specialty',
    image: '/badges/scs-badge.png', color: 'bg-red-600', icon: '🛡️'
  },
  {
    code: 'ANS-C01', name: 'Advanced Networking', category: 'Specialty',
    image: '/badges/ans-badge.png', color: 'bg-purple-500', icon: '🌐'
  }
]);

// This computed property automatically groups them so you don't have to!
const groupedCerts = computed(() => {
  const groups = {
    'Foundational': [],
    'Associate': [],
    'Professional': [],
    'Specialty': []
  };

  certifications.value.forEach(cert => {
    if (groups[cert.category]) {
      groups[cert.category].push(cert);
    }
  });

  return groups;
});

const userRank = computed(() => {
  const xp = userTotalXp.value;

  if (xp >= 5000) {
    return { title: 'Ascended Master', color: 'text-purple-600', bar: 'bg-gradient-to-r from-purple-500 to-fuchsia-500', min: 5000, max: 'MAX', progress: 100 };
  } else if (xp >= 1500) {
    return { title: 'Serverless Sorcerer', color: 'text-indigo-600', bar: 'bg-indigo-500', min: 1500, max: 5000, progress: ((xp - 1500) / 3500) * 100 };
  } else if (xp >= 500) {
    return { title: 'EC2 Gladiator', color: 'text-emerald-600', bar: 'bg-emerald-500', min: 500, max: 1500, progress: ((xp - 500) / 1000) * 100 };
  } else {
    return { title: 'Cloud Initiate', color: 'text-blue-600', bar: 'bg-blue-500', min: 0, max: 500, progress: (xp / 500) * 100 };
  }
});

// --- 2. The Selection Logic ---
// 1. THEY SELECT THE CERTIFICATION
const selectCertification = (cert) => {
  selectedCert.value = cert;
  selectedCategory.value = 'All';

  // We do NOT start the quiz yet! We just scroll down to reveal the menu.
  setTimeout(() => {
    const menu = document.getElementById('protocol-menu');
    if (menu) menu.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, 50);
};

const fetchLeaderboard = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/questions/leaderboard');
    if (response.ok) {
      leaderboardData.value = await response.json();
    }
  } catch (error) {
    console.error("Failed to contact the Jedi Archives for the leaderboard.", error);
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

    localStorage.setItem('aws_user', JSON.stringify(userData));
    showAuthModal.value = false;

  } catch (error) {
    authForm.value.passwordhash = ''; // Clear password on server crash too
    authError.value = "Communication disruption can mean only one thing: invasion. (Server offline)";
  }
};

const toggleAnswer = (optionId) => {
  // Guard: If no question is loaded or explanation is showing, do nothing
  if (!question.value || showExplanation.value) return;

  playSound('click');

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
      playSound('trash');
      // Refresh the table locally so the row disappears immediately
      examHistory.value = examHistory.value.filter(a => a.id !== attemptId);

    } else {
      alert("Failed to delete. Check if the backend endpoint exists.");
    }
  } catch (error) {
  }
};

const finishExam = async () => {
  // 1. Stop the clock
  if (timerInterval) clearInterval(timerInterval);

  // 3. Switch to results view
  currentView.value = 'quiz'; // We stay in the quiz component container
  showResults.value = true;   // But we trigger the Results overlay

  // 4. THE FIX: Pass the downloaded questions to the review array!
  reviewQuestions.value = questions.value;

  // 5. Save the data to the database
  await saveResults();

  // 6. Scroll to top so they see the score
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

const handlePrint = async () => { // <-- NOTE THE 'async' HERE!
  const doc = new jsPDF();

  // --- 1. FIND THE ACTIVE DATA ---
  const sourceQuestions = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  const totalQs = sourceQuestions.length || 1;
  const score = Math.round((correctCount.value / totalQs) * 100);

  // --- 2. BRANDING & HEADER (THE NEW ANIME BANNER) ---

  // Helper function to load the image into memory for jsPDF
  const loadImage = (url) => {
    return new Promise((resolve) => {
      const img = new Image();
      img.src = url;
      img.onload = () => resolve(img);
      img.onerror = () => {
        console.warn("Banner failed to load, falling back to blue.");
        resolve(null);
      };
    });
  };

  // Load the image from your public folder
  const bannerImg = await loadImage('/image/anime_banner.jpg');

  if (bannerImg) {
    // Draw the image: x=0, y=0, width=210mm (A4 size), height=45mm
    doc.addImage(bannerImg, 'JPEG', 0, 0, 210, 45);
  } else {
    // Fallback just in case the image isn't found
    doc.setFillColor(37, 99, 235);
    doc.rect(0, 0, 210, 40, 'F');
  }

  // Draw the Text (I added a slight drop-shadow effect to make it pop over the image!)
  doc.setFontSize(22);
  doc.setFont("helvetica", "bold");



  // --- 3. METADATA ---
  doc.setTextColor(15, 23, 42);
  doc.setFontSize(11);
  doc.setFont("helvetica", "normal");

  const candidateName = currentUser.value ? currentUser.value.username : 'Guest Candidate';
  const examName = selectedCert.value?.name || selectedCert.value?.code || 'AWS Practitioner (AIP-C01)';
  const dateStr = new Date().toLocaleDateString();

  // Shifted the Y-coordinates down slightly to account for the larger image banner
  doc.text(`Candidate ID: ${candidateName}`, 14, 55);
  doc.text(`Assessment Module: ${examName}`, 14, 62);
  doc.text(`Date of Execution: ${dateStr}`, 14, 69);

  doc.setFontSize(16);
  doc.setFont("helvetica", "bold");
  if (score >= 72) { // Updated to 72% for AWS standards!
    doc.setTextColor(22, 163, 74);
    doc.text(`Final Score: ${score}% (PASSED)`, 120, 62);
  } else {
    doc.setTextColor(220, 38, 38);
    doc.text(`Final Score: ${score}% (FAILED)`, 120, 62);
  }

  // --- 4. THE MISSED QUESTIONS TABLE ---

  const missedQs = sourceQuestions.filter(q => userResults.value[q.id] !== true);

  if (missedQs.length === 0) {
    doc.setTextColor(15, 23, 42);
    doc.text("Flawless Victory. No missed concepts to report.", 14, 90);
  } else {
    doc.setTextColor(15, 23, 42);
    doc.setFontSize(14);
    doc.text("Missed Concepts Analysis:", 14, 90);

    const tableData = missedQs.map((q, index) => [
      index + 1,
      q.questionText || "Question text missing",
      q.explanation || "No detailed explanation provided."
    ]);

    autoTable(doc, {
      startY: 95,
      head: [['#', 'Question Parameter', 'Detailed Explanation']],
      body: tableData,
      theme: 'grid',
      headStyles: { fillColor: [15, 23, 42], textColor: [255, 255, 255] },
      styles: { font: 'helvetica', fontSize: 10, cellPadding: 4 },
      columnStyles: {
        0: { cellWidth: 10, halign: 'center', fontStyle: 'bold' },
        1: { cellWidth: 70 },
        2: { cellWidth: 102 }
      },
    });
  }

  // --- 5. FOOTER ---
  const pageCount = doc.internal.getNumberOfPages();
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i);
    doc.setFontSize(8);
    doc.setTextColor(150);
    doc.text(
        `Generated by AWS.Hub Simulation Engine - Page ${i} of ${pageCount}`,
        14,
        doc.internal.pageSize.height - 10
    );
  }

  doc.save(`AWS_Hub_Report_${candidateName.replace(/\s+/g, '_')}.pdf`);
};

// 1. The API Call (Defined first, so other functions can see it)
const recordStudySession = async () => {
  if (!currentUser.value) return; // Don't track guests

  try {
    const response = await fetch(`http://localhost:8080/api/users/${currentUser.value.username}/record-study`, {
      method: 'POST',
      // headers: { 'Content-Type': 'application/json' }
    });

    if (response.ok) {
      const data = await response.json();
      // Update your Vue variable so the flame updates instantly without a page refresh!
      userStreak.value = data.currentStreak;
      console.log("Streak updated successfully:", data);

    } else {
      // 2. Let's force the console to print the EXACT Spring Boot error!
      const errorText = await response.text();
      console.error("Backend rejected the request:", errorText);
    }
  } catch (error) {
    console.error("Failed to record study session:", error);
  }
};

const awardExperiencePoints = async () => {
  if (!currentUser.value) return;

  // Calculate how many they got right (assuming you have finalScore and totalExamQuestions variables!)
  const correctCount = Math.round((finalScore.value / 100) * totalExamQuestions.value);

  try {
    const response = await fetch(`http://localhost:8080/api/users/${currentUser.value.username}/award-xp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        correctCount: correctCount,
        totalQuestions: totalExamQuestions.value
      })
    });

    if (response.ok) {
      const data = await response.json();
      userTotalXp.value = data.totalXp; // Instantly fills the progress bar!
      console.log(`Earned ${data.earnedXp} XP! Total is now: ${data.totalXp}`);
    }
  } catch (error) {
    console.error("Failed to award XP:", error);
  }
};

// 2. The Grading Sequence (Which now safely calls the function above)
const executeGradingSequence = async () => {
  stopTimer();
  localStorage.removeItem('aws_active_exam');

  // THE FIX: Stop the AMRAP clock ticking in the background!
  if (amrapInterval) {
    clearInterval(amrapInterval);
  }

  showGradeConfirmModal.value = false;
  showResults.value = true;
  isHistoricalView.value = false;

  await recordStudySession();
  await awardExperiencePoints();

  if (currentUser.value && currentUser.value.id) {
    await saveResults();
  } else {
    console.error("CRITICAL FAILURE: No active user ID found in session.");
  }
};

// 3. The Force Grade trigger
const forceGradeExam = () => {
  executeGradingSequence();
};

// --- Computed Results ---

// 1. Calculate how many questions were answered perfectly
// 1. We use 'computed' so Vue knows this is a live math formula!
const correctCount = computed(() => {
  // THE FIX: Point the formula to the array where the questions actually live!
  const activeQuestions = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  if (!activeQuestions || !Array.isArray(activeQuestions) || activeQuestions.length === 0) return 0;

  return activeQuestions.filter(q => {
    if (!selectedAnswersRecord.value || !selectedAnswersRecord.value[q.id]) return false;

    const correctOptionIds = q.options
        .filter(opt => opt.isCorrect === true || String(opt.isCorrect).toLowerCase() === 'true')
        .map(opt => String(opt.id));

    const userSelectedIds = selectedAnswersRecord.value[q.id].map(id => String(id));

    return correctOptionIds.length === userSelectedIds.length &&
        correctOptionIds.every(id => userSelectedIds.includes(id));
  }).length;
});

// 2. Automatically calculate the percentage based on the formula above
const finalScore = computed(() => {
  // THE FIX: Ensure the percentage math also uses the correct array!
  const activeQuestions = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  const totalQs = activeQuestions?.length || 1;
  return Math.round((correctCount.value / totalQs) * 100);
});

// 3. Determine if they passed the AWS standard (72%)
const isPassing = computed(() => scorePercentage.value >= 72);

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const response = await fetch('http://localhost:8080/api/admin/upload-csv', {
      method: 'POST',
      body: formData
    });

    if (response.ok) {
      alert("Database Updated: New questions have been synced.");
    } else {
      alert("Sync Failed: Check CSV format.");
    }
  } catch (error) {
  }
};

const closeReview = () => {
  // 1. Turn off all the Results and Historical screens
  showResults.value = false;
  isHistoricalView.value = false;

  // If you are using a separate modal variable, turn it off too
  if (typeof showReviewModal !== 'undefined') {
    showReviewModal.value = false;
  }

  currentView.value = 'landing';

  reviewQuestions.value = [];

  if (typeof fetchHistory === 'function') fetchHistory();
  if (typeof fetchLeaderboard === 'function') fetchLeaderboard();
};

const submitAuth = async () => {
  authError.value = ''; // Reset errors
  const endpoint = isLoginMode.value ? '/api/auth/login' : '/api/auth/register';

  try {
    const response = await fetch(`http://localhost:8080${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(authForm.value)
    });

    if (!response.ok) {
      const errorText = await response.text();
      authError.value = errorText || "Authentication failed. Check your credentials.";
      return;
    }

    const data = await response.json();

    // 1. Save the VIP Pass (JWT) to the browser's secret storage
    localStorage.setItem('aws_jwt', data.token);
    authToken.value = data.token;

    // 2. Update the local user stats
    currentUser.value = {
      username: data.username,
      xp: data.xp
    };
    localStorage.setItem('aws_user', JSON.stringify(currentUser.value));

    // 3. Clean up and close the modal
    showAuthModal.value = false;
    authForm.value = { username: '', email: '', password: '' }; // clear form
    alert(`Welcome to the Knowledge Forge, ${data.username}!`);

  } catch (error) {
    console.error("Auth System Offline:", error);
    authError.value = "Cannot connect to the server. Is Spring Boot running?";
  }
};

const logout = () => {
  localStorage.removeItem('aws_jwt');
  localStorage.removeItem('aws_user');
  authToken.value = null;
  currentUser.value = null;
  alert("You have been logged out.");
};

clearInterval(amrapInterval);

// --- 1. THE WATCHER ---
watch(selectedCert, (newCert) => {
  if (newCert && currentUser.value) {
    if (typeof fetchQuestion === 'function' && currentView.value === 'quiz') {
      fetchQuestion();
    }
  }
}, { immediate: true });

// --- 2. THE BOOT SEQUENCE ---
// Only ONE onMounted block is allowed!
onMounted(async () => {

  history.replaceState({ view: currentView.value }, '', `#${currentView.value}`);
  // A. Always fetch global Hall of Fame for the dashboard

  if (typeof fetchHallOfFame === 'function') {
    fetchHallOfFame();
  }

  // B. Check the browser's permanent memory for a returning user!
  const savedUser = localStorage.getItem('aws_user');

  if (savedUser) {
    currentUser.value = JSON.parse(savedUser);
    showAuthModal.value = false; // Hide the login screen!

    // C. Fetch their personal history now that we know who they are
    if (typeof fetchHistory === 'function') {
      await fetchHistory();
    }

    // D. Resume quiz if they refreshed the page while looking at a cert
    if (selectedCert.value && typeof fetchQuestion === 'function') {
      fetchQuestion();
    }
  }

  fetchLeaderboard();
});

onMounted(() => {
  history.replaceState({ view: currentView.value }, '', `#${currentView.value}`);
});

// 2. Watch your view variable. Whenever you change screens, push a new entry to the browser!
watch(currentView, (newView) => {
  // Only push to history if it's actually a new screen
  if (history.state === null || history.state.view !== newView) {
    history.pushState({ view: newView }, '', `#${newView}`);
  }
});

// 3. Hijack the Browser's Back/Forward Arrows
window.addEventListener('popstate', (event) => {
  if (event.state && event.state.view) {
    // If they click 'Back', read the history and change the Vue screen to match
    currentView.value = event.state.view;
  } else {
    // Failsafe: If they go back too far, dump them safely at the Command Center
    currentView.value = 'landing';
  }
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