<template>
  <div class="min-h-screen bg-gray-50 pb-12 px-4 font-sans relative">

    <div v-if="showAuthModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-md flex items-center justify-center z-[200] p-4">
      <div class="bg-gray-900 border border-blue-500 rounded-2xl p-8 w-full max-w-md text-white shadow-[0_0_30px_rgba(59,130,246,0.3)] relative">
        <button @click="showAuthModal = false" class="absolute top-4 right-4 text-gray-400 hover:text-white text-3xl leading-none">&times;</button>
        <h2 class="text-3xl font-black text-center mb-6 text-blue-400 tracking-tight">
          {{ isLoginMode ? 'Access Datapad' : 'Register Recruit' }}
        </h2>
        <div v-if="authError" class="bg-red-900/50 border border-red-500 text-red-200 p-3 rounded-xl mb-6 text-sm text-center font-bold animate-pulse">
          {{ authError }}
        </div>
        <form @submit.prevent="submitAuth" class="space-y-5">
          <div>
            <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Codename (Username)</label>
            <input v-model="authForm.username" type="text" required class="w-full bg-gray-800 border border-gray-600 rounded-xl p-4 text-white font-bold focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all" />
          </div>
          <div v-if="!isLoginMode">
            <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Comm-Link (Email)</label>
            <input v-model="authForm.email" type="email" placeholder="Optional" class="w-full bg-gray-800 border border-gray-600 rounded-xl p-4 text-white font-bold focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all" />
          </div>
          <div>
            <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Passcode</label>
            <input v-model="authForm.password" type="password" required class="w-full bg-gray-800 border border-gray-600 rounded-xl p-4 text-white font-bold focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all" />
          </div>
          <button type="submit" class="w-full bg-blue-600 hover:bg-blue-500 text-white font-black text-lg py-4 rounded-xl shadow-[0_0_15px_rgba(37,99,235,0.5)] transition-all hover:-translate-y-1 mt-2">
            {{ isLoginMode ? 'Login' : 'Create Account' }}
          </button>
        </form>
        <div class="text-center mt-6">
          <button @click="isLoginMode = !isLoginMode; authError = ''" class="text-sm font-bold text-slate-400 hover:text-white transition-colors">
            {{ isLoginMode ? 'Need an account? Register here.' : 'Already a recruit? Login here.' }}
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-5xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4 mb-6 bg-white p-6 md:p-4 rounded-3xl shadow-sm border border-slate-100 mt-6">
      <div class="flex items-center gap-2 px-4 flex-shrink-0">
        <div class="flex items-center gap-2 px-4 flex-shrink-0" @dblclick="currentView = 'adminlogin'">
          <span class="text-3xl md:text-2xl font-black text-blue-600 tracking-tighter italic cursor-pointer hover:opacity-80 transition-opacity" title="System Mainframe">AWS.Hub</span>
        </div>
      </div>

      <div v-if="currentUser" class="flex flex-col md:flex-row items-center gap-3">

        <div class="flex items-center gap-2 px-4 py-2.5 bg-slate-50 rounded-xl border border-slate-200 shadow-sm transition-all hover:shadow-md">
          <span class="text-2xl transition-all duration-500" :class="[streakDisplay.color, streakDisplay.aura]">{{ streakDisplay.icon }}</span>
          <div class="flex flex-col text-left">
            <span class="text-[10px] font-black uppercase tracking-wider text-slate-400 leading-none mb-1">Daily Streak</span>
            <span class="text-sm font-bold text-slate-800 leading-none">
            {{ userStreak }} Days <span class="text-[10px] font-normal italic opacity-75 ml-1 hidden lg:inline-block">({{ streakDisplay.label }})</span>
          </span>
          </div>
        </div>

        <div class="flex items-center justify-between gap-4 mb-2">
          <div class="flex flex-col">
            <span class="text-[10px] font-black uppercase tracking-[0.2em] mb-0.5" :class="userRank.color">{{ userRank.title }}</span>
            <span class="text-xl font-black text-slate-900 tracking-tighter leading-none drop-shadow-sm truncate max-w-[120px]">{{ currentUser?.username }}</span>
          </div>
          <div class="h-8 w-px bg-slate-200 hidden md:block"></div>

          <button @click="logout" class="group relative flex flex-col items-center">
            <span class="text-[9px] font-black text-red-500 uppercase tracking-widest transition-all group-hover:text-red-700">Logout</span>
            <span class="h-0.5 w-0 bg-red-500 transition-all group-hover:w-full"></span>
          </button>
        </div>

         <button @click="toggleMusic" class="p-2 rounded-full hover:bg-slate-100 transition-colors group" :title="isMusicPlaying ? 'Pause Focus Track' : 'Play Focus Track'">
          <span class="text-lg opacity-70 group-hover:opacity-100 transition-opacity" :class="isMusicPlaying ? 'animate-pulse text-blue-500' : 'grayscale'">🎧</span>
        </button>

        <button @click="toggleSFX" class="p-2 rounded-full hover:bg-slate-100 transition-colors group" :title="isAudioEnabled ? 'Mute Sound Effects' : 'Enable Sound Effects'">
          <span class="text-lg transition-all" :class="isAudioEnabled ? 'opacity-100 text-amber-500' : 'opacity-40 grayscale'">🔊</span>
        </button>
      </div>

      <div v-else class="flex items-center gap-4 px-4">
        <button @click="showAuthModal = true; isLoginMode = true; authError = ''" class="text-sm font-bold text-slate-500 hover:text-blue-600 transition-colors">Log In</button>
        <button @click="showAuthModal = true; isLoginMode = false; authError = ''" class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-black px-6 py-2.5 rounded-xl shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">Sign Up</button>
      </div>
    </div>

    <div v-if="selectedCert && currentView === 'landing'" id="protocol-menu" class="mt-8 mb-16 bg-white rounded-[2rem] p-6 md:p-8 border border-slate-200 shadow-xl animate-fade-in-up flex flex-col md:flex-row gap-8 max-w-5xl mx-auto">
      <div class="flex-shrink-0 flex flex-col items-center justify-center bg-slate-50 p-8 rounded-[1.5rem] border border-slate-100 md:w-1/3">
        <div :class="[selectedCert.color, 'w-24 h-24 rounded-2xl shadow-lg flex items-center justify-center text-5xl mb-4 transform transition-transform hover:scale-105']">
          {{ selectedCert.icon }}
        </div>
        <h3 class="text-xl font-black text-slate-900 text-center leading-tight mb-1">{{ selectedCert.name }}</h3>
        <span class="text-xs font-bold px-3 py-1 bg-slate-200 text-slate-600 rounded-full uppercase tracking-wider mb-6">{{ selectedCert.code }}</span>
        <button @click="selectedCert = null" class="mt-auto w-full group relative overflow-hidden flex flex-col items-center justify-center bg-white hover:bg-slate-50 p-4 rounded-2xl transition-all border-2 border-slate-100 hover:border-slate-300 shadow-sm hover:shadow-md">

          <div class="flex items-center gap-2 text-sm font-black text-slate-800 uppercase tracking-widest mb-1 relative z-10">
            <span class="group-hover:-translate-x-1.5 transition-transform text-slate-400 group-hover:text-slate-800 text-lg">←</span>
            Switch Certification
          </div>

          <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider text-center relative z-10">
        Return to roster & view scores
      </span>

          <div class="absolute inset-0 bg-gradient-to-t from-slate-100/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
        </button>
      </div>

      <div class="flex-grow flex flex-col gap-3">
        <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1 px-2">Select Training Protocol</h4>

        <button @click="initiateProtocol('standard')" class="relative overflow-hidden group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-blue-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] font-black px-3 py-1 rounded-bl-xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl bg-blue-50 text-blue-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-blue-500 group-hover:text-white transition-all">🏛️</span>
          <div class="pr-16">
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-blue-600 transition-colors">Simulated Exam</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">Standard assessment. Timed. Real exam conditions.</p>
          </div>
        </button>

        <button @click="initiateProtocol('amrap')" class="relative overflow-hidden group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-red-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] font-black px-3 py-1 rounded-bl-xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl bg-red-50 text-red-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-red-500 group-hover:text-white transition-all">⏱️</span>
          <div class="pr-16">
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-red-600 transition-colors">AMRAP Attack</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">10 minute clock. Infinite questions. Pure speed and recall.</p>
          </div>
        </button>

        <button @click="initiateProtocol('sudden_death')" class="relative overflow-hidden group flex items-center gap-4 bg-white p-4 rounded-2xl border-2 border-purple-100 hover:border-purple-500 hover:shadow-xl hover:shadow-purple-500/20 hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-emerald-400 to-green-500 text-white text-[10px] font-black px-4 py-1 rounded-bl-xl tracking-widest uppercase shadow-[0_0_15px_rgba(16,185,129,0.6)] animate-pulse">
            ⚡ Free Play
          </div>
          <span class="text-2xl bg-purple-50 text-purple-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-purple-500 group-hover:text-white transition-all">💀</span>
          <div class="pr-20">
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-purple-600 transition-colors">Sudden Death</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">3 Lives. No timer. One mistake shatters a heart. Survive.</p>
          </div>
        </button>

        <button @click="initiateProtocol('library')" class="relative overflow-hidden group flex items-center gap-4 bg-white p-4 rounded-2xl border border-slate-200 hover:border-emerald-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] font-black px-3 py-1 rounded-bl-xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl bg-emerald-50 text-emerald-500 w-12 h-12 flex flex-shrink-0 items-center justify-center rounded-xl group-hover:scale-110 group-hover:bg-emerald-500 group-hover:text-white transition-all">🧠</span>
          <div class="pr-16">
            <h5 class="text-sm font-bold text-slate-900 group-hover:text-emerald-600 transition-colors">The Knowledge Forge</h5>
            <p class="text-[11px] text-slate-500 font-medium leading-tight mt-0.5">Access study guides, architectural diagrams, and video modules.</p>
          </div>
        </button>
      </div>
    </div>

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
            <div v-for="cert in certs" :key="cert.code" @click="selectCertification(cert)" class="group cursor-pointer bg-white rounded-[2.5rem] p-8 shadow-sm border border-slate-100 hover:shadow-2xl hover:-translate-y-2 transition-all flex flex-col items-center text-center">
              <div class="h-28 w-28 md:h-40 md:w-40 mb-6 flex items-center justify-center relative">
                <img v-if="cert.image" :src="cert.image" @error="cert.image = null" class="w-full h-full object-contain drop-shadow-2xl group-hover:scale-110 transition-transform duration-300" />
                <div v-else :class="['w-full h-full rounded-3xl flex items-center justify-center text-5xl text-white shadow-lg', cert.color]">{{ cert.icon }}</div>
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
                      <span :class="['px-3 py-1 rounded-lg font-black text-sm border', attempt.scorePercentage >= 75 ? 'bg-green-50 text-green-600 border-green-100' : 'bg-red-50 text-red-600 border-red-100']">{{ attempt.scorePercentage }}%</span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <span class="bg-amber-50 text-amber-700 text-[10px] font-black uppercase px-2 py-1 rounded-md border border-amber-100">{{ attempt.weakestCategory || 'MIXED' }}</span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <button @click="deleteAttempt(attempt.id)" class="text-slate-400 hover:text-red-600 hover:scale-125 transition-all text-lg" title="Delete this attempt">🗑️</button>
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
          <h3 class="text-2xl font-black text-slate-900 mb-6 flex items-center gap-2">✨ Ascended Masters</h3>
          <div v-if="leaderboardData.length > 0" class="space-y-3">
            <div v-for="(master, index) in leaderboardData" :key="index" class="flex items-center justify-between p-4 bg-slate-50 border border-slate-100 rounded-xl hover:shadow-md transition-all">
              <div class="flex items-center gap-4">
                <div class="w-8 h-8 rounded-full flex items-center justify-center font-black text-sm shadow-sm" :class="index === 0 ? 'bg-gradient-to-br from-amber-200 to-amber-400 text-amber-900' : index === 1 ? 'bg-gradient-to-br from-slate-200 to-slate-400 text-slate-800' : index === 2 ? 'bg-gradient-to-br from-orange-200 to-orange-400 text-orange-900' : 'bg-blue-100 text-blue-600'">#{{ index + 1 }}</div>
                <div>
                  <p class="font-black text-slate-800">{{ master.fullName }}</p>
                  <p class="text-[10px] font-bold text-slate-400 tracking-widest uppercase">{{ master.examCode }}</p>
                </div>
              </div>
              <div class="text-xl font-black" :class="master.scorePercentage === 100 ? 'text-emerald-500' : 'text-slate-700'">{{ master.scorePercentage }}%</div>
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
              <optgroup label="Foundational"><option value="CLF-C02">Cloud Practitioner (CLF-C02)</option><option value="AIF-C01">AI Practitioner (AIF-C01)</option></optgroup>
              <optgroup label="Associate"><option value="SAA-C03">Solutions Architect (SAA-C03)</option><option value="DVA-C02">Developer (DVA-C02)</option><option value="SOA-C02">SysOps Admin (SOA-C02)</option><option value="DEA-C01">Data Engineering (DEA-C01)</option></optgroup>
              <optgroup label="Professional"><option value="SAP-C02">Solutions Architect Pro (SAP-C02)</option><option value="DOP-C02">DevOps Engineer Pro (DOP-C02)</option><option value="AIP-C01">Generative AI Dev (AIP-C01)</option></optgroup>
              <optgroup label="Specialty"><option value="MLS-C01">Machine Learning (MLS-C01)</option><option value="SCS-C02">Security (SCS-C02)</option><option value="ANS-C01">Advanced Networking (ANS-C01)</option></optgroup>
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
              <div v-for="(priority, index) in studyPriorities" :key="priority.name" @click="jumpToForge(priority.name)" :class="index === 0 ? 'bg-red-50 border border-red-100 text-red-900 hover:bg-red-100' : 'bg-amber-50 border border-amber-100 text-amber-900 hover:bg-amber-100'" class="flex items-center justify-between p-4 rounded-xl shadow-sm transition-all hover:shadow-md cursor-pointer hover:-translate-y-1 group" title="Click to study this module in The Knowledge Forge">
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
            <div v-else class="text-center p-6 text-slate-400 text-sm font-bold">Insufficient telemetry data. Complete a simulation to generate priorities.</div>
          </div>
        </div>
      </div>

    </div>

    <div v-else-if="currentView === 'quiz'" class="max-w-5xl mx-auto px-4 md:px-8 lg:px-12">
      <div v-if="showResults && !isHistoricalView" class="bg-white rounded-[2rem] shadow-2xl p-10 text-center border border-gray-100">
        <div class="mb-10 text-center flex flex-col items-center">
          <div class="mb-8 flex justify-center w-full px-2">
            <img v-if="examResultMessage.image" :src="examResultMessage.image" class="w-full max-w-sm md:max-w-2xl h-auto object-cover rounded-2xl shadow-2xl border border-slate-100/50" alt="Result Animation" />
            <div v-else class="text-7xl md:text-8xl">{{ examResultMessage.icon }}</div>
          </div>
          <h1 :class="['text-3xl md:text-5xl font-black mb-4 tracking-tight', examResultMessage.color]">{{ examResultMessage.title }}</h1>
          <div class="bg-slate-50 border border-slate-100 p-6 rounded-2xl max-w-2xl mx-auto shadow-inner">
            <p class="text-slate-600 font-mono text-sm md:text-base leading-relaxed italic">"{{ examResultMessage.text }}"</p>
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
          <button @click="openReviewRoom(lastAttemptId)" class="bg-orange-500 hover:bg-amber-500 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">🔍 Review My Answers</button>
          <button @click="handlePrint" class="bg-slate-800 hover:bg-slate-900 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">📄 Save Results as PDF</button>
          <button @click="resetSession" class="bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-indigo-600 text-white py-4 rounded-2xl font-black shadow-lg hover:shadow-xl hover:scale-[1.02] transition-all duration-300">Start New Session</button>
          <button @click="goBackToLanding" class="bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-800 py-4 rounded-2xl font-bold transition-colors duration-300 border border-slate-200">Return to Dashboard</button>
        </div>
      </div>

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
              <span v-for="n in 3" :key="n" class="text-xl transition-all duration-500 ease-[cubic-bezier(0.34,1.56,0.64,1)]" :class="n <= healthPoints ? 'text-red-500 scale-100 drop-shadow-[0_0_8px_rgba(239,68,68,0.8)]' : 'opacity-20 scale-75 grayscale'">❤️</span>
            </div>
            <div v-else-if="isAmrapMode" class="flex items-center gap-2 bg-red-600/20 px-4 py-1.5 rounded-xl border border-red-500/50 shadow-[0_0_15px_rgba(220,38,38,0.3)]">
              <span class="text-red-500 animate-pulse">⏱️</span>
              <span class="font-mono font-black text-red-100 tracking-widest text-lg">{{ formattedAmrapTime }}</span>
            </div>
            <span v-else class="font-mono font-bold bg-blue-500/50 px-3 py-1 rounded-lg border border-blue-400/50">{{ formattedCurrentTime }}</span>
            <button @click="togglePause" class="bg-white text-blue-600 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg">{{ isPaused ? 'Resume' : 'Pause' }}</button>
            <button @click="showGradeConfirmModal = true" class="bg-slate-900/40 hover:bg-slate-900/60 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg transition-colors border border-white/10">🏁 Grade Now</button>

            <button @click="abortQuiz" class="bg-red-500/20 hover:bg-red-500/40 text-red-100 px-3 py-1 rounded-lg text-xs font-black uppercase tracking-widest shadow-lg transition-colors border border-red-400/30">🚪 Exit</button>
          </div>
        </div>

        <div class="p-8">
          <p class="text-xl md:text-3xl lg:text-3xl font-extrabold text-slate-800 mb-8 md:mb-12 leading-relaxed md:leading-tight">{{ question.questionText }}</p>
          <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)" :class="['w-full text-left p-4 md:p-6 lg:p-8 rounded-2xl md:rounded-[2rem] border-2 transition-all flex items-center gap-4 md:gap-6', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50' : 'border-slate-100 bg-slate-50 hover:border-blue-200 hover:shadow-md']">
            <div class="w-6 h-6 border-2 flex items-center justify-center flex-shrink-0" :class="[isMultipleAnswer(question) ? 'rounded-md' : 'rounded-full', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-500 text-white' : 'border-slate-300 bg-white']">
              <span v-if="isMultipleAnswer(question) && selectedAnswers.includes(opt.id)">✓</span>
              <div v-if="!isMultipleAnswer(question) && selectedAnswers.includes(opt.id)" class="w-2.5 h-2.5 bg-white rounded-full"></div>
            </div>
            <span class="text-base md:text-lg lg:text-xl font-bold text-slate-700">{{ opt.text || opt.optionText || opt.value }}</span>
          </button>
          <button v-if="!showExplanation" @click="checkAnswer" :disabled="selectedAnswers.length === 0" class="w-full mt-8 py-4 rounded-2xl font-black text-white bg-blue-600 transition-all duration-300 ease-in-out hover:-translate-y-1 hover:shadow-[0_10px_40px_-10px_rgba(37,99,235,0.6)] active:translate-y-1 active:shadow-sm disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0">Submit Answer</button>

          <div v-if="showExplanation" class="mt-8">
            <div :class="['p-4 rounded-2xl text-center font-black uppercase mb-4', feedbackClass]">{{ feedback }}</div>
            <p class="bg-amber-50 p-5 rounded-2xl text-sm italic border border-amber-100 text-slate-700">{{ question.explanation }}</p>
            <button @click="loadNextQuestion" class="w-full mt-4 bg-green-600 text-white py-4 rounded-2xl font-black shadow-lg">Next Question ➔</button>
            <button @click="forceGradeExam" class="w-full mt-6 bg-slate-100 text-slate-500 py-3 rounded-2xl font-bold border-2 border-dashed border-slate-200 hover:bg-slate-200 hover:text-slate-700 transition-all">🏁 Grade Now</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentView === 'library'" class="max-w-7xl mx-auto px-4 md:px-8 py-6 animate-fade-in-up">

      <div class="flex flex-col md:flex-row justify-between items-center mb-6 gap-4 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 px-6 py-4 rounded-2xl shadow-lg shadow-indigo-500/20 relative overflow-hidden text-white">
        <div class="absolute inset-0 bg-white/5 backdrop-blur-sm mix-blend-overlay"></div>
        <div class="flex items-center gap-4 relative z-10">
          <div class="w-12 h-12 bg-white/20 backdrop-blur-md rounded-xl flex items-center justify-center shadow-inner border border-white/30 transform hover:scale-105 transition-transform">
            <span class="text-2xl">{{ selectedCert ? selectedCert.icon : '🧠' }}</span>
          </div>
          <div>
            <h2 class="text-xl font-black tracking-tight mb-0">
              {{ selectedCert ? selectedCert.code + ' Knowledge Forge' : 'The Knowledge Forge' }}
            </h2>
            <p class="text-indigo-100 font-medium text-[10px] uppercase tracking-widest flex items-center gap-1.5 mt-0.5">
              <span class="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse shadow-[0_0_8px_rgba(74,222,128,1)]"></span>
              Premium Databanks Unlocked
            </p>
          </div>
        </div>

      </div>

      <div v-if="selectedCert && selectedCert.domains" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

        <div
            v-for="(domain, index) in selectedCert.domains"
            :key="index"
            @click="handleDomainClick(domain, index)"
            class="relative bg-white p-5 rounded-3xl border shadow-sm hover:shadow-xl transition-all cursor-pointer group transform hover:-translate-y-1 flex flex-col overflow-hidden"
            :class="[(index > 0 && !(currentUser && currentUser.isPremium)) ? 'border-slate-200 opacity-95' : 'border-slate-200 hover:border-indigo-400']"
        >

          <div v-if="index === 0 && !(currentUser && currentUser.isPremium)" class="absolute top-0 right-0 bg-gradient-to-r from-emerald-400 to-green-500 text-white text-[9px] font-black px-3 py-1 rounded-bl-xl z-30 uppercase tracking-widest shadow-[0_0_10px_rgba(16,185,129,0.4)] animate-pulse">
            ⚡ Free Preview
          </div>

          <div v-if="index > 0" class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[9px] font-black px-3 py-1 rounded-bl-xl z-30 uppercase tracking-widest shadow-[0_0_10px_rgba(245,158,11,0.4)]">
            ⭐ Premium
          </div>

          <div v-if="index > 0 && !(currentUser && currentUser.isPremium)" class="absolute inset-0 z-20 bg-slate-50/70 backdrop-blur-[2px] flex flex-col items-center justify-center transition-all group-hover:bg-slate-50/50">
            <div class="bg-white p-4 rounded-full shadow-lg text-2xl mb-2 group-hover:scale-110 transition-transform">🔒</div>
            <span class="bg-indigo-600 text-white text-[10px] font-black px-3 py-1 rounded-full tracking-widest uppercase shadow-md shadow-indigo-500/30">Premium Required</span>
          </div>

          <div class="bg-slate-50 h-36 rounded-2xl mb-5 flex flex-col items-center justify-center group-hover:bg-indigo-50 transition-colors border border-slate-100 relative overflow-hidden">
        <span class="text-4xl opacity-40 group-hover:opacity-100 group-hover:scale-110 transition-all drop-shadow-sm">
          {{ domain.icon || '📁' }}
        </span>
          </div>

          <span class="text-[9px] font-black tracking-widest uppercase px-2.5 py-1 rounded-full mb-3 inline-block self-start"
                :class="[(index > 0 && !(currentUser && currentUser.isPremium)) ? 'bg-slate-100 text-slate-500' : 'bg-indigo-50 text-indigo-600']">
        Domain {{ index + 1 }}
      </span>

          <h3 class="text-base font-black text-slate-900 mb-1.5 leading-tight"
              :class="[(index > 0 && !(currentUser && currentUser.isPremium)) ? '' : 'group-hover:text-indigo-600 transition-colors']">
            {{ domain.name }}
          </h3>
          <p class="text-[11px] text-slate-500 font-medium line-clamp-2">
            {{ domain.description }}
          </p>
        </div>

      </div>
    </div>

    <div v-else-if="currentView === 'adminlogin'" class="max-w-md mx-auto mt-20 p-8 bg-slate-900 rounded-[2rem] shadow-2xl border border-slate-800 text-center animate-fade-in-up">
      <div class="w-16 h-16 bg-red-500/10 text-red-500 rounded-2xl flex items-center justify-center text-3xl mx-auto mb-6 border border-red-500/20 shadow-inner">🔒</div>
      <h2 class="text-2xl font-black text-white tracking-widest uppercase mb-2">Director Access</h2>
      <p class="text-slate-400 text-sm mb-8 font-mono">Enter authorization code to access the mainframe.</p>

      <form @submit.prevent="handleAdminAuth" class="space-y-5">
        <input
            type="password"
            v-model="adminPasscode"
            placeholder="Enter Passcode..."
            class="w-full bg-slate-950 border border-slate-800 rounded-xl p-4 text-center text-white font-mono tracking-widest focus:outline-none focus:border-red-500 focus:ring-1 focus:ring-red-500 transition-all shadow-inner"
        />
        <p v-if="adminLoginError" class="text-red-500 text-[10px] font-black uppercase tracking-widest animate-pulse">{{ adminLoginError }}</p>

        <button type="submit" class="w-full bg-red-600 hover:bg-red-700 text-white py-4 rounded-xl font-black tracking-widest uppercase transition-all hover:-translate-y-0.5 shadow-lg shadow-red-500/20">
          Authenticate
        </button>
      </form>

      <button @click="currentView = 'landing'" class="mt-8 text-slate-500 hover:text-white text-xs uppercase tracking-widest transition-colors">
        ← Return to Base
      </button>
    </div>

    <div v-else-if="currentView === 'admin_hub'" class="max-w-5xl mx-auto px-4 py-12 animate-fade-in-up">

      <div class="mb-8 flex items-center justify-between bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">System Admin Hub</h2>
          <p class="text-slate-500 font-medium text-sm mt-1">Manage the AWS Knowledge Matrix</p>
        </div>
        <button @click="currentView = 'landing'" class="bg-slate-100 hover:bg-slate-200 text-slate-600 px-5 py-2.5 rounded-xl font-bold text-xs uppercase tracking-widest transition-colors border border-slate-200">
          ← Return to Base
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

        <button @click="currentView = 'admin'; fetchAdminQuestions();" class="bg-white p-8 rounded-[2rem] border border-slate-200 shadow-sm hover:shadow-xl hover:-translate-y-1 hover:border-indigo-400 transition-all text-left group">
          <div class="w-16 h-16 bg-indigo-50 text-indigo-600 rounded-2xl flex items-center justify-center text-3xl mb-6 group-hover:scale-110 transition-transform">⚙️</div>
          <h3 class="text-xl font-black text-slate-900 mb-2">Question Database</h3>
          <p class="text-slate-500 text-sm leading-relaxed">Manually inject, edit, or vaporize individual questions in the master data node.</p>
        </button>

        <div class="bg-white p-8 rounded-[2rem] border border-slate-200 shadow-sm flex flex-col transition-all hover:shadow-md">
          <div class="w-16 h-16 bg-emerald-50 text-emerald-600 rounded-2xl flex items-center justify-center text-3xl mb-6">📁</div>
          <h3 class="text-xl font-black text-slate-900 mb-2">Bulk CSV Upload</h3>
          <p class="text-slate-500 text-sm leading-relaxed mb-6">Expand the Jedi Archives instantly. Upload formatted CSV files to populate the database.</p>

          <div class="mt-auto">
            <input type="file" id="csv-upload" accept=".csv" class="hidden" @change="handleFileUpload" />
            <label for="csv-upload" class="cursor-pointer inline-flex w-full items-center justify-center gap-2 bg-slate-800 hover:bg-slate-950 text-white px-6 py-4 rounded-xl font-black transition-all shadow-md hover:shadow-lg hover:-translate-y-1">
              <svg class="w-5 h-5 text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
              Select CSV File
            </label>
          </div>
        </div>

      </div>
    </div>

    <div v-else-if="currentView === 'admin'" class="max-w-7xl mx-auto px-4 py-8 animate-fade-in admin-dashboard">
      <div class="admin-header">
        <h2>SYSTEM MAINFRAME: QUESTION DATABASE</h2>
        <button @click="openAdminModal(null)" class="btn-create">+ INJECT NEW QUESTION</button>
      </div>

      <table class="admin-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>Cert</th>
          <th>Category</th>
          <th>Question Text</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="q in allAdminQuestions" :key="q.id">
          <td>{{ q.id }}</td>
          <td><span class="badge">{{ q.examCode }}</span></td>
          <td>{{ q.category }}</td>
          <td class="truncate">{{ q.text }}</td>
          <td class="action-buttons">
            <button @click="openAdminModal(q)" class="btn-edit">EDIT</button>
            <button @click="deleteAdminQuestion(q.id)" class="btn-delete">VAPORIZE</button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="showAdminModal" class="admin-modal-overlay">
        <div class="admin-modal-content">
          <h3>{{ isAdminEditing ? 'EDIT DATA NODE' : 'INJECT NEW DATA NODE' }}</h3>
          <label>Certification Code</label>
          <input v-model="adminForm.examCode" placeholder="e.g. SCS-C02" />
          <label>Category</label>
          <input v-model="adminForm.category" placeholder="e.g. Security" />
          <label>Question Text</label>
          <textarea v-model="adminForm.text" rows="3"></textarea>
          <label>Correct Answer</label>
          <input v-model="adminForm.correctAnswer" />
          <label>Explanation</label>
          <textarea v-model="adminForm.explanation" rows="2"></textarea>
          <div class="modal-actions">
            <button @click="showAdminModal = false" class="btn-cancel">CANCEL</button>
            <button @click="saveAdminQuestion" class="btn-save">OVERWRITE MAINFRAME</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="currentView === 'lessonViewer' && activeDomain" class="max-w-7xl mx-auto px-4 md:px-8 py-6 animate-fade-in-up">

      <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8 border-b border-slate-200 pb-6">
        <div>
          <button @click="currentView = 'library'" class="mb-3 text-[10px] font-black text-slate-400 hover:text-indigo-600 transition-colors uppercase tracking-widest flex items-center gap-1.5 group">
            <span class="group-hover:-translate-x-1 transition-transform">←</span> Return to Databanks
          </button>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-3">
            <span class="text-indigo-500">{{ activeDomain.icon }}</span>
            {{ activeDomain.name }}
          </h2>
        </div>
      </div>

      <div class="flex flex-col lg:flex-row gap-8">

        <div class="lg:w-2/3 flex flex-col gap-6">

          <div class="aspect-video bg-slate-900 rounded-3xl shadow-2xl relative overflow-hidden group border border-slate-800">

            <div v-if="!isPlaying" class="absolute inset-0 flex flex-col items-center justify-center z-20">
              <button @click="isPlaying = true" class="w-20 h-20 bg-indigo-600/90 hover:bg-indigo-500 backdrop-blur-md rounded-full flex items-center justify-center transition-all hover:scale-110 shadow-[0_0_30px_rgba(79,70,229,0.5)] cursor-pointer">
                <span class="text-white text-3xl ml-2">▶</span>
              </button>
              <p class="text-slate-300 font-bold tracking-widest uppercase text-[10px] mt-6 bg-slate-900/50 px-4 py-1.5 rounded-full backdrop-blur-sm border border-slate-700">
                {{ activeModule?.type === 'video' ? 'Initiate Video Protocol' : 'Access Databank Document' }}
              </p>
            </div>

            <div v-else class="absolute inset-0 z-30 bg-black animate-fade-in">

              <iframe
                  v-if="activeModule?.type === 'video'"
                  class="w-full h-full"
                  :src="activeModule.videoUrl || 'https://www.youtube.com/embed/k1RI5locZE4?autoplay=1'"
                  title="AWS Training Module"
                  frameborder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen>
              </iframe>

              <div v-else class="w-full h-full flex flex-col bg-slate-100 overflow-hidden relative">

                <div class="bg-white px-6 py-4 border-b border-slate-200 flex justify-between items-center z-10 shadow-sm shrink-0">
                  <h3 class="font-black text-slate-800 text-lg flex items-center gap-2">
                    📄 {{ activeModule?.title || 'Classified Document' }}
                  </h3>
                  <div class="flex gap-3">
                    <button @click="downloadDocumentPDF" class="flex items-center gap-2 bg-slate-800 hover:bg-slate-900 text-white px-4 py-2 rounded-lg font-bold text-xs uppercase tracking-widest transition-all hover:-translate-y-0.5 shadow-md">
                      ⬇️ Save PDF
                    </button>
                    <button @click="completeActiveModule" class="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 rounded-lg font-bold text-xs uppercase tracking-widest transition-all hover:-translate-y-0.5 shadow-md shadow-indigo-500/30">
                      ✓ Claim XP
                    </button>
                  </div>
                </div>

                <div class="p-4 md:p-8 overflow-y-auto flex-grow flex justify-center bg-slate-100/50">
                  <div class="bg-white w-full max-w-3xl p-8 md:p-12 rounded-xl shadow-md border border-slate-200 min-h-full">
                    <div class="prose prose-slate max-w-none font-serif text-slate-700 whitespace-pre-wrap leading-relaxed">
                      {{ activeModule?.content || 'Awaiting transmission... No document content found.' }}
                    </div>
                  </div>
                </div>

              </div>

            </div>

            <div v-if="!isPlaying" class="absolute inset-0 bg-gradient-to-br from-indigo-900/40 via-slate-900 to-purple-900/40"></div>
          </div>

          <div v-if="activeModule" class="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
        <span class="text-[10px] font-black text-indigo-600 tracking-widest uppercase bg-indigo-50 px-3 py-1 rounded-full mb-3 inline-block">
          Current Objective
        </span>
            <h3 class="text-2xl font-black text-slate-900 mb-2">{{ activeModule.title }}</h3>
            <p class="text-sm text-slate-500 font-medium leading-relaxed">
              Focus on understanding the core principles presented in this module. Take notes on key terminology as it will heavily influence your Sudden Death survival rate.
            </p>
          </div>
        </div>

        <div class="lg:w-1/3">
          <div class="bg-white rounded-3xl border border-slate-200 shadow-sm sticky top-8 overflow-hidden flex flex-col max-h-[800px]">

            <div class="p-6 border-b border-slate-100 bg-slate-50/50">
              <h4 class="text-sm font-black text-slate-900 uppercase tracking-widest">Training Modules</h4>
              <p class="text-[11px] text-slate-500 mt-1 font-medium">{{ activeDomain?.modules?.length || 0 }} protocols available</p>
            </div>

            <div class="overflow-y-auto p-3 flex flex-col gap-2">
              <button
                  v-for="(module, index) in activeDomain?.modules"
                  :key="module.id"
                  @click="activeModule = module"
                  class="w-full flex items-start gap-4 p-4 rounded-2xl transition-all text-left group"
                  :class="activeModule?.id === module.id ? 'bg-indigo-50 border border-indigo-100 shadow-sm' : 'hover:bg-slate-50 border border-transparent hover:border-slate-100'"
              >
                <div class="mt-0.5 shrink-0">
                  <span v-if="activeModule?.id === module.id" class="flex h-6 w-6 items-center justify-center rounded-full bg-indigo-600 text-white shadow-md shadow-indigo-500/30 text-[10px]">▶</span>
                  <span v-else-if="module.type === 'document'" class="flex h-6 w-6 items-center justify-center rounded-full bg-slate-100 text-slate-400 group-hover:text-slate-600 text-[10px]">📄</span>
                  <span v-else class="flex h-6 w-6 items-center justify-center rounded-full bg-slate-100 text-slate-400 group-hover:text-slate-600 text-[10px]">📺</span>
                </div>

                <div class="flex-grow">
                  <h5 class="text-sm font-bold leading-tight mb-1 flex items-center gap-2 flex-wrap" :class="activeModule?.id === module.id ? 'text-indigo-900' : 'text-slate-700 group-hover:text-slate-900'">
                    <span>{{ index + 1 }}. {{ module.title }}</span>

                    <span
                        class="text-[9px] uppercase tracking-wider font-black px-1.5 py-0.5 rounded border"
                        :class="module.type === 'video' ? 'bg-indigo-100 text-indigo-600 border-indigo-200' : 'bg-emerald-100 text-emerald-600 border-emerald-200'"
                    >
          {{ module.type === 'video' ? '▶ Video' : '📄 Doc' }}
        </span>
                  </h5>

                  <span class="text-[10px] font-bold tracking-wider uppercase text-slate-400" :class="activeModule?.id === module.id ? 'text-indigo-400' : ''">
        {{ module.duration }}
      </span>
                </div>
              </button>

              <div v-if="!activeDomain?.modules || activeDomain.modules.length === 0" class="text-center p-8 text-slate-400">
                <p class="text-xs font-bold uppercase tracking-widest">No modules loaded</p>
              </div>

            </div>

          </div>
        </div>

      </div>
    </div>

    <div v-if="showGuestLimitModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-md z-[400] flex items-center justify-center p-4">
      <div class="bg-slate-900 border border-cyan-500/50 rounded-[2rem] w-full max-w-md overflow-hidden shadow-[0_0_30px_rgba(6,182,212,0.3)] relative p-10 text-center">
        <div class="text-5xl mb-4 animate-pulse">🛑</div>
        <h2 class="text-2xl font-black text-cyan-400 mb-4 font-mono uppercase tracking-widest">Free Compute Exhausted</h2>
        <p class="text-slate-300 mb-8 font-mono text-sm leading-relaxed italic">"You've taken your first step into a larger world..."<br><br><span class="not-italic text-slate-400 text-xs">However, unauthorized access to the deeper Jedi Archives is restricted. Register your biometric signature to unlock the full simulator and save your history.</span></p>
        <div class="flex flex-col gap-3">
          <button @click="showGuestLimitModal = false; showAuthModal = true; isSignup = true" class="w-full bg-cyan-600 text-white py-4 rounded-xl font-black shadow-[0_0_15px_rgba(6,182,212,0.5)] hover:bg-cyan-500 hover:scale-[1.02] transition-all uppercase tracking-widest text-sm">Create Account</button>
          <button @click="showGuestLimitModal = false; goBackToLanding()" class="w-full bg-slate-800 text-slate-400 py-4 rounded-xl font-bold border border-slate-700 hover:bg-slate-700 hover:text-white transition-all uppercase tracking-widest text-xs">Return to Base</button>
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
                <div class="absolute left-0 top-0 w-8 h-8 rounded-full bg-gradient-to-br from-blue-600 to-blue-500 text-white flex items-center justify-center font-black text-sm shadow-md">{{ index + 1 }}</div>
                <h3 class="text-xl md:text-2xl font-extrabold text-slate-900 leading-tight">Question Details</h3>
              </div>
              <span :class="userResults[q.id] ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'" class="font-black uppercase text-xs tracking-wider px-3 py-1 rounded-full flex items-center gap-1.5">{{ userResults[q.id] ? '✓ Correct' : '✗ Incorrect' }}</span>
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
            <button @click="handlePrint" class="w-full md:w-auto flex items-center justify-center gap-2 bg-slate-800 hover:bg-slate-950 text-white py-4 px-10 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">Export to PDF</button>
            <button @click="closeReview" class="w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white py-4 px-12 rounded-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">✓ Got it! Close Review</button>
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

          <button @click="showSuccessHologram = false" class="w-full bg-cyan-900/40 hover:bg-cyan-400 text-cyan-400 hover:text-black border border-cyan-400 py-4 rounded-xl font-black tracking-widest uppercase transition-all duration-300 shadow-[0_0_15px_rgba(34,211,238,0.2)] hover:shadow-[0_0_30px_rgba(34,211,238,0.6)] group">
            <span class="group-hover:animate-pulse">Enter Databanks</span>
          </button>

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
              <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>Warning: Premature Termination
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
            <p class="text-slate-300 font-bold tracking-wide">Execute final telemetry calculation anyway?</p>
          </div>
        </div>
        <div class="bg-slate-950 p-6 md:p-8 border-t border-slate-800 flex flex-col md:flex-row justify-end gap-4">
          <button @click="showGradeConfirmModal = false" class="px-6 py-3 rounded-xl font-mono text-sm font-bold text-slate-400 hover:text-white hover:bg-slate-800 transition-colors border border-transparent hover:border-slate-700">[ ESC ] ABORT</button>
          <button @click="executeGradingSequence" class="group relative px-8 py-3 rounded-xl font-mono text-sm font-black text-red-100 bg-red-600 overflow-hidden shadow-[0_0_20px_rgba(220,38,38,0.4)] hover:shadow-[0_0_30px_rgba(220,38,38,0.6)] transition-all">
            <div class="absolute inset-0 bg-white/20 translate-y-full group-hover:translate-y-0 transition-transform duration-300 ease-in-out"></div>
            <span class="relative z-10">> EXECUTE_GRADE</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="showPremiumModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" @click="showPremiumModal = false"></div>

      <div class="relative bg-white rounded-3xl shadow-2xl w-full max-w-4xl overflow-hidden animate-fade-in-up flex flex-col md:flex-row">

        <div class="bg-slate-900 p-8 md:p-12 text-white md:w-1/2 flex flex-col justify-center relative overflow-hidden">
          <div class="absolute top-0 right-0 w-64 h-64 bg-indigo-500/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>

          <h2 class="text-3xl md:text-4xl font-black mb-4 leading-tight">
            Unlock the <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-purple-400">Knowledge Forge</span>
          </h2>
          <p class="text-slate-400 font-medium mb-8">
            Stop guessing. Get the exact architectural breakdowns, video modules, and survival guides you need to dominate your AWS certification.
          </p>

          <ul class="space-y-4 font-medium text-sm text-slate-300">
            <li class="flex items-center gap-3"><span class="flex h-6 w-6 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs">✓</span> Unlimited Video Modules</li>
            <li class="flex items-center gap-3"><span class="flex h-6 w-6 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs">✓</span> High-Res Architecture Diagrams</li>
            <li class="flex items-center gap-3"><span class="flex h-6 w-6 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs">✓</span> Downloadable Cheat Sheets</li>
          </ul>
        </div>

        <div class="p-8 md:p-12 md:w-1/2 bg-slate-50 flex flex-col justify-center gap-6">
          <button @click="showPremiumModal = false" class="absolute top-4 right-4 text-slate-400 hover:text-slate-600 transition-colors">
            <span class="text-2xl font-black">×</span>
          </button>

          <div class="relative bg-white rounded-2xl p-6 border-2 border-indigo-500 shadow-[0_0_30px_rgba(99,102,241,0.15)] transition-all hover:-translate-y-1 group cursor-pointer" @click="initiateCheckout('trial')">
            <div class="absolute -top-3 left-1/2 -translate-x-1/2 bg-indigo-500 text-white text-[10px] font-black uppercase tracking-widest px-3 py-1 rounded-full">
              Most Popular
            </div>
            <div class="flex justify-between items-end mb-2">
              <h3 class="text-lg font-black text-slate-800">7-Day Trial</h3>
              <div class="text-right">
                <span class="text-3xl font-black text-indigo-600">$1</span>
              </div>
            </div>
            <p class="text-sm text-slate-500 font-medium mb-4">Test the waters. Renews at $10.99/mo. Cancel anytime.</p>
            <button class="w-full py-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold tracking-wide transition-all shadow-md group-hover:shadow-indigo-500/30">
              Start $1 Trial
            </button>
          </div>

          <div class="bg-white rounded-2xl p-6 border border-slate-200 transition-all hover:-translate-y-1 hover:border-slate-300 hover:shadow-lg cursor-pointer" @click="initiateCheckout('monthly')">
            <div class="flex justify-between items-end mb-2">
              <h3 class="text-lg font-bold text-slate-700">Monthly Pro</h3>
              <div class="text-right">
                <span class="text-2xl font-black text-slate-800">$10.99</span>
                <span class="text-xs text-slate-400 font-bold">/mo</span>
              </div>
            </div>
            <p class="text-sm text-slate-500 font-medium mb-4">Jump straight into the deep end. Cancel anytime.</p>
            <button class="w-full py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl font-bold tracking-wide transition-all">
              Subscribe Monthly
            </button>
          </div>

        </div>
      </div>
    </div>

    <div v-if="showUpgradePrompt" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/80 backdrop-blur-sm animate-fade-in">
      <div class="bg-white rounded-3xl p-8 max-w-md w-full shadow-2xl relative border border-slate-100 text-center animate-fade-in-up">

        <div class="w-20 h-20 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center text-4xl mx-auto mb-6 shadow-inner">
          🚀
        </div>

        <h2 class="text-2xl font-black text-slate-800 mb-3">Unlock Knowledge Forge</h2>

        <p class="text-slate-600 mb-8 font-medium leading-relaxed">
          Premium access is required to enter the Forge. Start your 7-day trial for just <span class="font-black text-indigo-600 text-lg">$1</span>, then $10.99/mo. Cancel anytime.
        </p>

        <div class="flex gap-4 mt-4">
          <button @click="showUpgradePrompt = false" class="flex-1 py-3.5 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-xl font-bold transition-colors">
            Cancel
          </button>

          <button @click="handleSignUpClick" class="flex-1 py-3.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold shadow-md shadow-indigo-500/30 transition-all hover:-translate-y-0.5">
            Sign Up
          </button>
        </div>

      </div>
    </div>

    <div v-if="showAlreadyPremiumModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/80 backdrop-blur-sm"
    >
      <div class="bg-white rounded-3xl p-8 max-w-md w-full shadow-2xl border border-slate-100 text-center animate-fade-in-up">

        <div class="text-6xl mb-4">🌟</div>
        <h3 class="text-2xl font-bold text-slate-800 mb-2">You're Already Premium!</h3>
        <p class="text-slate-500 mb-8 leading-relaxed">
          Your account is already fully upgraded. You have unrestricted access to the Knowledge Forge and all Premium features.
        </p>

        <div class="flex flex-col space-y-3">
          <button
              @click="showAlreadyPremiumModal = false"
              class="w-full bg-blue-600 text-white font-bold py-3 px-4 rounded-xl hover:bg-blue-700 transition-all duration-200 transform hover:-translate-y-0.5 shadow-lg shadow-blue-500/30"
          >
            Return to Dashboard
          </button>

          <button
              @click="() => {
          showAlreadyPremiumModal = false;
          // Clears memory to simulate logging out
          localStorage.removeItem('aws_jwt');
          localStorage.removeItem('aws_user');
          window.location.reload();
        }"
              class="w-full bg-slate-100 text-slate-600 font-bold py-3 px-4 rounded-xl hover:bg-slate-200 transition-colors"
          >
            Log Out / Switch Account
          </button>
        </div>

      </div>
    </div>

  </div>

  <Transition name="glitch-fade">
  <div v-if="showLevelUp" class="cyber-overlay">
    <div class="cyber-modal">
      <div class="scanlines"></div>
      <div class="cyber-header">⚠ SECURE CONNECTION ESTABLISHED ⚠</div>
      <h1 class="cyber-title">SYSTEM MASTERY</h1>
      <h2 class="cyber-subtitle">MODULE COMPLETED</h2>
      <div class="xp-container">
        <span class="xp-plus">+</span>
        <span class="xp-number">500</span>
        <span class="xp-label">XP</span>
      </div>
      <p class="cyber-user">DATA UPLOADED TO OPERATIVE: <span class="neon-text">[{{ currentUser.username }}]</span></p>
      <button @click="showLevelUp = false" class="cyber-btn">ACKNOWLEDGE</button>
    </div>
  </div>
</Transition>

</template>

<script setup>
  import { ref, onMounted, computed, watch, onUnmounted } from 'vue';
  import { jsPDF } from "jspdf";
  import autoTable from 'jspdf-autotable';
  import { Radar } from 'vue-chartjs';
  import { Chart as ChartJS, RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend } from 'chart.js';

  ChartJS.register(RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend);

  // 1. THE MACRO: defineProps must always be the very first thing
  const props = defineProps({
  isLoggedIn: { type: Boolean, default: false },
  isPremium: { type: Boolean, default: false },
  username: { type: String, default: '' }
});

  const currentUser = ref(null);

  watch(() => [props.isLoggedIn, props.username, props.isPremium],([isLoggedIn, username, isPremium]) => {

        // A. Rebuild the user
        if (isLoggedIn && username) {
          currentUser.value = {
            ...currentUser.value,
            username: username,
            isPremium: isPremium
          };
        } else if (!isLoggedIn) {
          currentUser.value = null;
        }

        // B. The Auto-Launcher (Bulletproof Version)
        if (isPremium === true && localStorage.getItem('aws_awaiting_stripe_return') === 'true') {

          localStorage.removeItem('aws_awaiting_stripe_return');

          const savedCert = localStorage.getItem('aws_return_cert');
          const savedView = localStorage.getItem('aws_return_view') || 'library'; // Default to library if lost!

          if (savedCert && savedCert !== 'undefined') {
            selectedCert.value = JSON.parse(savedCert);
            localStorage.removeItem('aws_return_cert');
          }

          setTimeout(() => {
            // 🚨 NO MORE TRAPDOORS: We route them based strictly on where they left!
            if (savedView === 'library') {
              console.log("Stripe return detected! Forcing entry to Knowledge Forge...");
              currentView.value = 'library';
              if (typeof fetchKnowledgeForge === 'function' && selectedCert.value) {
                fetchKnowledgeForge(selectedCert.value.code);
              }
            } else {
              console.log("Stripe return detected! Auto-launching Sudden Death...");
              if (typeof initiateProtocol === 'function') {
                initiateProtocol('sudden_death');
              }
            }

            localStorage.removeItem('aws_return_view');

            // 🚨 URL SCRUBBER: Erase Stripe's "?success=true" from the URL so Vue doesn't get confused on refresh!
            window.history.replaceState({}, document.title, window.location.pathname + "#" + currentView.value);

          }, 500);
        }
      },
      { immediate: true }
  ); // 🚨 THIS IS THE FIX! You were missing this closing parenthesis and semicolon!

  // 🚨 WATCHER 2: "The Seamless Bridge"

  watch(currentUser, (newUser) => {
    // If a user just logged in, they are NOT premium yet, AND they have the memory flag...
    if (newUser && !newUser.isPremium && localStorage.getItem('aws_pending_checkout') === 'true') {

      // 1. Erase the memory flag so this doesn't loop
      localStorage.removeItem('aws_pending_checkout');

      // 2. Automatically fire the checkout sequence!
      console.log("Seamless Bridge Activated: Warping new user to Stripe...");

      // Add a tiny delay so the Auth modal has time to close smoothly
      setTimeout(() => {
        if (typeof handleSignUpClick === 'function') {
          handleSignUpClick();
        }
      }, 300);
    }
  });

// --- Core State ---
const question = ref(null);
const showAlreadyPremiumModal = ref(false);
const adminPasscode = ref('');
const adminLoginError = ref('');

const selectedAnswers = ref([]);
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
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
const showUpgradePrompt = ref(false);
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
const activeDomain = ref(null);

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
const currentView = ref(window.location.hash.replace('#', '') || 'landing');
const selectedCert = ref(null);
const showGuestLimitModal = ref(false);
const hallOfFame = ref([]);

// --- Timer Logic ---
const timer = ref(0);

// JWT AUTHENTICATION ENGINE
const showAuthModal = ref(false);
const showPremiumModal = ref(false);
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
const isSignup = ref(true);
const userResults = ref({});

  // 2. THE SYNCHRONIZER: When App.vue sends new data, update our local user!
  watch(() => props.isLoggedIn, (newStatus) => {
    if (newStatus && props.username) {
      currentUser.value = {
        ...currentUser.value,
        username: props.username,
        isPremium: props.isPremium
      };
    }
    // 🚨 THE FIX: We deleted the 'else' block that was prematurely wiping your user!
  }, { immediate: true });

  // 3. THE UPGRADE CATCHER & AUTO-LAUNCHER
  watch(() => props.isPremium, (newPremium) => {
    if (currentUser.value) {
      currentUser.value.isPremium = newPremium;
    }

    // 🚨 THE SMART BREADCRUMB: Wait for Java to confirm Premium before launching!
    if (newPremium === true && localStorage.getItem('aws_awaiting_stripe_return') === 'true') {

      // 1. Clean up the breadcrumbs
      localStorage.removeItem('aws_awaiting_stripe_return');
      const savedCert = localStorage.getItem('aws_return_cert');

      if (savedCert) {
        // 2. Restore the selection and launch!
        selectedCert.value = JSON.parse(savedCert);
        localStorage.removeItem('aws_return_cert');

        console.log("Stripe return detected! Auto-launching Sudden Death...");
        initiateProtocol('sudden_death');
      }
    }
  }, { immediate: true });

const allAdminQuestions = ref([]);
const showAdminModal = ref(false);
const isAdminEditing = ref(false);

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
const libraryModules = ref([]);

// --- DYNAMIC TARGETED FETCH ---
  // --- DYNAMIC TARGETED FETCH ---
  // --- DYNAMIC TARGETED FETCH ---
  const fetchKnowledgeForge = async (targetCertCode) => {
    if (!targetCertCode) return;

    try {
      // 1. Fetch the logged-in user's progress history (This route is protected!)
      let completedModules = [];
      if (currentUser.value && currentUser.value.username) {
        const actualToken = localStorage.getItem('aws_jwt');
        const progressResponse = await fetch(`http://localhost:8080/api/progress/${currentUser.value.username}`, {
          headers: { 'Authorization': `Bearer ${actualToken}` }
        });

        if (progressResponse.ok) {
          completedModules = await progressResponse.json();
        }
      }

      // 🚨 THE FIX: Force the 5 custom modules to load BEFORE asking the database!
      if (targetCertCode === 'SCS-C02') {
        console.warn("Forcing custom SCS-C02 Matrix curriculum...");

        const scsDomains = [
          { id: 'scs-domain-1', title: 'Identity and Access Management', icon: '🔐', description: 'Master IAM policies, cross-account roles, AWS SSO, and federated identity. The absolute foundation of all AWS security architecture.', lessonCount: 4, studyGuide: 'IAM Foundation Guide' },
          { id: 'scs-domain-2', title: 'Threat Detection & Incident Response', icon: '🚨', description: 'Deploy Amazon GuardDuty, AWS Security Hub, and automate incident response using EventBridge and Lambda to neutralize threats.', lessonCount: 5, studyGuide: 'Threat Mitigation Guide' },
          { id: 'scs-domain-3', title: 'Infrastructure Security', icon: '🧱', description: 'Secure your VPCs, master AWS Network Firewalls, WAF, Shield Advanced, and lock down your EC2 and container environments.', lessonCount: 6, studyGuide: 'Infrastructure Hardening' },
          { id: 'scs-domain-4', title: 'Data Protection', icon: '💎', description: 'Encrypt data at rest and in transit using AWS KMS, CloudHSM, and master Macie for discovering sensitive data across S3.', lessonCount: 4, studyGuide: 'Cryptography & KMS' },
          { id: 'scs-domain-5', title: 'Security Logging & Monitoring', icon: '📡', description: 'Centralize your visibility using CloudTrail, VPC Flow Logs, and CloudWatch to ensure total architectural transparency.', lessonCount: 3, studyGuide: 'Visibility & Auditing' }
        ];

        libraryModules.value = scsDomains.map(mod => ({
          ...mod,
          progress: completedModules.includes(mod.id) ? 100 : 0,
          videos: Array.from({ length: mod.lessonCount }).map((_, i) => ({ title: `Video Lesson ${i + 1}`, id: `vid-${i}` }))
        }));

        return; // 🚨 EXIT EARLY! Do not ask the database!
      }

      // 3. For all OTHER certifications, ask the database normally
      const response = await fetch(`http://localhost:8080/api/modules/cert/${targetCertCode}`);

      if (response.ok) {
        const dbModules = await response.json();
        libraryModules.value = dbModules.map(mod => ({
          id: mod.id,
          title: mod.title,
          icon: mod.icon,
          description: mod.description,
          lessonCount: mod.videoIds ? mod.videoIds.length : 0,
          progress: completedModules.includes(mod.id) ? 100 : 0,
          studyGuide: mod.studyGuideContent,
          videos: mod.videoIds ? mod.videoIds.map((vidId, i) => ({ title: `Video Lesson ${i + 1}`, id: vidId })) : []
        }));
      } else {
        libraryModules.value = [];
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

// --- Achievement Modal State ---
const showLevelUp = ref(false);

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

// Add this to determine if radio buttons should be squares or circles
const isMultipleAnswer = (question) => {
  if (!question || !question.questionText) return false;

  // Checks if the AWS question explicitly asks for multiple answers
  return question.questionText.includes("Select TWO") ||
      question.questionText.includes("Select THREE") ||
      question.questionText.includes("Choose 2") ||
      question.questionText.includes("Choose 3");
};

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

// 🎮 THE ARCADE ROSTER: Controls the gates for every game
const arcadeConfig = {
    sudden_death: {
      name: 'Sudden Death',
      requiresPremium: false,
      maxFreePlays: 3
    },
    library: {
      name: 'The Knowledge Forge',
      requiresPremium: true
    },
    flashcards: {
      name: 'Flashcard Matrix',
      requiresPremium: true
    },
    standard: { name: 'Simulated Exam', requiresPremium: true },
    amrap: { name: 'AMRAP Attack', requiresPremium: true }
    // Add future games here! (e.g., matching_game: { requiresPremium: true })
  };

const handleAdminAuth = () => {
    // The secret passcode to access the hub!
    if (adminPasscode.value === 'masterkey') {
      currentView.value = 'admin_hub';
      adminPasscode.value = '';
      adminLoginError.value = '';
    } else {
      adminLoginError.value = 'ACCESS DENIED: Invalid Director clearance.';
      adminPasscode.value = '';
    }
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
  // Function to handle clicking a card
  const openModule = (moduleId) => {
    // 1. Find the exact module they clicked and its index in the array
    const targetIndex = libraryModules.value.findIndex(m => m.id === moduleId);
    const targetModule = libraryModules.value[targetIndex];

    if (!targetModule) return;

    // 2. THE BOUNCER: Is this a locked module?
    // It is locked if it's NOT the first module (index 0) AND they aren't Premium
    const isLocked = targetIndex > 0 && (!currentUser.value || !currentUser.value.isPremium);

    if (isLocked) {
      // 3. Play a denied sound (optional, but satisfying)
      if (typeof playSound === 'function') playSound('wrong');

      // 4. Offer the upsell!
      const wantsToUpgrade = confirm("🔒 PREMIUM ARCHIVE LOCKED\n\nThis module contains advanced AWS concepts reserved for Premium members.\n\nWould you like to establish a secure connection to the Stripe Gateway and upgrade your access?");

      if (wantsToUpgrade) {
        upgradeToPremium();
      }
      return; // Abort! Do not let them open the video player!
    }

    // 5. If it's the free module OR they are Premium, let them in!
    if (typeof playSound === 'function') playSound('click');
    activeModule.value = targetModule;
    currentVideoIndex.value = 0;
    currentView.value = 'lessonViewer';
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

// --- COMPLETE MODULE PROGRESSION ---
const completeActiveModule = async () => {
  // 1. Security Check: Are they logged in?
  if (!currentUser.value || !currentUser.value.username) {
    alert("Warning: Guest mode active. Progress not saved to the Mainframe.");
    return;
  }

  try {
    // 2. Send the completion data to Spring Boot
    const response = await fetch(`http://localhost:8080/api/progress/complete?username=${currentUser.value.username}&moduleId=${activeModule.value.id}`, {
      method: 'POST',
      headers: {
        // THIS IS THE VIP PASS!
        'Authorization': `Bearer ${authToken.value}` // (If this throws an error, change to: localStorage.getItem('aws_jwt') )
      }
    });

    if (response.ok) {
      // 3. Update the local UI (Fill the bar instantly!)
      activeModule.value.progress = 100;

      // 4. Award 500 XP and save it locally
      userTotalXp.value += 500;
      currentUser.value.xp = userTotalXp.value;
      localStorage.setItem('aws_user', JSON.stringify(currentUser.value));

      // 5. TRIGGER THE NEON MAINFRAME OVERLAY (Alert is gone!)
      showLevelUp.value = true;

      // Auto-close the overlay after 4 seconds
      setTimeout(() => {
        showLevelUp.value = false;
      }, 4000);

      // 6. Send them back to the grid smoothly
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

const toggleSFX = () => {
    isAudioEnabled.value = !isAudioEnabled.value;
    if (isAudioEnabled.value) {
      playSound('click'); // Give them auditory feedback that it turned back on!
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

const analyzeCategoryMastery = async () => {
  // 1. Get the active questions
  const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  // 2. Build the payload for Spring Boot
  const updates = [];

  activeQs.forEach(q => {
    updates.push({
      category: q.category,
      isCorrect: userResults.value[q.id] === true
    });
  });

  // 3. Send the raw attempt data to the server
  if (currentUser.value && currentUser.value.username) {
    try {
      const actualToken = localStorage.getItem('aws_jwt');
      const response = await fetch(`http://localhost:8080/api/progress/${currentUser.value.username}/radar/update`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${actualToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updates)
      });

      if (response.ok) {
        console.log("Mastery data successfully synced to Jedi Archives.");
        // 4. Re-download the newly calculated lifetime stats from the database!
        await fetchRadarStats();
      } else {
        console.error("Backend rejected the radar update.");
      }

    } catch (error) {
      console.error("Failed to sync radar stats:", error);
    }
  } else {
    console.warn("Guest mode: Radar stats not saved to database.");
  }
};

const syncXpToDatabase = async (correctCount, totalQuestions) => {
  // 1. Grab the VIP pass and the User Profile from the vault
  const actualToken = localStorage.getItem('aws_jwt');
  const savedUser = JSON.parse(localStorage.getItem('aws_user'));

  // If they aren't logged in, just silently return (they play as a guest)
  if (!actualToken || !savedUser) {
    console.warn("Guest mode active: XP saved locally only.");
    return;
  }

  try {
    // 2. Target the exact endpoint Spring Boot is guarding
    const url = `http://localhost:8080/api/progress/award-xp?username=${savedUser.username}&correctCount=${correctCount}&totalQuestions=${totalQuestions}`;

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${actualToken}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.ok) {
      const updatedUser = await response.json();

      // 3. Update the local vault with the new OFFICIAL database XP
      savedUser.xp = updatedUser.xp;
      localStorage.setItem('aws_user', JSON.stringify(savedUser));

      // 4. Update your reactive UI variables so the screen updates instantly!
      if (currentUser.value) {
        currentUser.value.xp = updatedUser.xp;
      }
      console.log(`🛡️ Server Sync Complete! New Lifetime XP: ${updatedUser.xp}`);
    } else {
      console.error("XP Sync blocked by server firewall.");
    }
  } catch (error) {
    console.error("Network Error: Could not reach the server.", error);
  }
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

const adminForm = ref({
  id: null,
  text: '',
  category: '',
  examCode: 'SCS-C02', // Defaulting to Security
  correctAnswer: '',
  explanation: ''
});

// 1. READ: Download all questions
const fetchAdminQuestions = async () => {
  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/admin/questions`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) {
      allAdminQuestions.value = await response.json();
    }
  } catch (error) {
    console.error("Admin Matrix Offline:", error);
  }
};

// 2. CREATE & UPDATE: Save the form to the database
const saveAdminQuestion = async () => {
  const token = localStorage.getItem('aws_jwt');
  const url = isAdminEditing.value
      ? `http://localhost:8080/api/admin/questions/${adminForm.value.id}`
      : 'http://localhost:8080/api/admin/questions';

  const method = isAdminEditing.value ? 'PUT' : 'POST';

  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(adminForm.value)
    });

    if (response.ok) {
      await fetchAdminQuestions(); // Refresh the table!
      showAdminModal.value = false; // Close the modal
    }
  } catch (error) {
    console.error("Failed to save question:", error);
  }
};

// 3. DELETE: Vaporize a question
const deleteAdminQuestion = async (id) => {
  if (!confirm("⚠ VAPORIZE QUESTION? This cannot be undone.")) return;

  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/admin/questions/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.ok) {
      await fetchAdminQuestions(); // Refresh the table!
    }
  } catch (error) {
    console.error("Failed to vaporize:", error);
  }
};

// 4. UI HELPER: Open the modal for editing or creating
const openAdminModal = (question = null) => {
  if (question) {
    adminForm.value = { ...question }; // Copy the existing question
    isAdminEditing.value = true;
  } else {
    // Reset to blank form for a new question
    adminForm.value = { id: null, text: '', category: '', examCode: '', correctAnswer: '', explanation: '' };
    isAdminEditing.value = false;
  }
  showAdminModal.value = true;
};

// --- FETCH LIFETIME RADAR STATS FROM SPRING BOOT ---
const fetchRadarStats = async () => {
  if (!currentUser.value || !currentUser.value.username) return;

  try {
    const actualToken = localStorage.getItem('aws_jwt');
    const response = await fetch(`http://localhost:8080/api/progress/${currentUser.value.username}/radar`, {
      headers: {
        'Authorization': `Bearer ${actualToken}`
      }
    });

    if (response.ok) {
      const dbStats = await response.json();

      // Update the reactive variable so the Radar Chart instantly redraws!
      categoryScores.value = dbStats;

      console.log("Radar stats loaded from Jedi Archives:", dbStats);
    }
  } catch (error) {
    console.error("Failed to load lifetime stats:", error);
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
  // 🛡️ THE MASTER GATEKEEPER
  const initiateProtocol = (targetView) => {
    const gameConfig = arcadeConfig[targetView];

    // If the view isn't in our roster (like the 'landing' page), just let them go
    if (!gameConfig) {
      currentView.value = targetView;
      return;
    }

    const isPremiumUser = currentUser.value && currentUser.value.isPremium;

    // --- GATE 1: THE PREMIUM WALL ---
    if (gameConfig.requiresPremium && !isPremiumUser) {
      if (typeof playSound === 'function') playSound('wrong');

      showUpgradePrompt.value = true;

      return;
    }

    // --- GATE 2: THE 3-STRIKE TRACKER (For Free/Unregistered Users) ---
    if (!isPremiumUser && gameConfig.maxFreePlays) {
      // 1. Read their local stamp card (default to 0 if it doesn't exist)
      const strikes = parseInt(localStorage.getItem(`aws_${targetView}_strikes`) || '0');

      // 2. Are they out of strikes?
      if (strikes >= gameConfig.maxFreePlays) {
        if (typeof playSound === 'function') playSound('wrong');

        // 🚨 SAME HERE! Fire the beautiful modal when they run out of free plays!
        showUpgradePrompt.value = true;

        return; // Abort!
      }

      // 3. They are allowed in! Punch their stamp card.
      localStorage.setItem(`aws_${targetView}_strikes`, (strikes + 1).toString());
      console.log(`🎫 Free play granted for ${gameConfig.name}: Strike ${strikes + 1} of ${gameConfig.maxFreePlays}`);
    }

    // --- VIP ACCESS GRANTED ---
    // If they passed the gates (or are Premium), let them play!
    if (typeof playSound === 'function') playSound('click');
    // 🚨 THE NEW WIRING: If it's a quiz game, boot the engine! If not, just change the view.
    if (['standard', 'amrap', 'sudden_death'].includes(targetView)) {
      bootQuizEngine(targetView);
    } else {
      currentView.value = targetView;
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // 🚨 THE NEW ENGINE STARTER
  const bootQuizEngine = async (mode) => {
    // 1. Reset all previous state
    resetSession();

    // 2. Set the active game mode
    activeProtocol.value = mode;
    isSuddenDeath.value = (mode === 'sudden_death');
    isAmrapMode.value = (mode === 'amrap');

    // 3. Configure the specific rules of the game
    if (mode === 'sudden_death') {
      healthPoints.value = 3;
      startTimer(); // Standard count-up timer
    }
    else if (mode === 'amrap') {
      startAmrapTimer(); // 10-minute countdown timer
    }
    else if (mode === 'standard') {
      totalExamQuestions.value = 65; // Standard AWS Exam length
      startTimer();
    }

    // 4. Change the screen to the unified quiz UI
    currentView.value = 'quiz';

    // 5. Fetch the very first question!
    await fetchQuestion();
  };

// 1. Add the Certifications list
const certifications = ref([
    // --- FOUNDATIONAL ---
    {
      code: 'CLF-C02', name: 'Cloud Practitioner', category: 'Foundational',
      image: '/badges/clf-badge.png', color: 'bg-orange-400', icon: '☁️',
      domains: [
        { name: 'Cloud Concepts', description: 'Define the AWS Cloud, its value proposition, and basic cloud economics.', icon: '💡', modules: [] },
        { name: 'Security and Compliance', description: 'Shared responsibility model, access management, and security capabilities.', icon: '🛡️', modules: [] },
        { name: 'Technology and Services', description: 'Core AWS services spanning compute, network, database, and storage.', icon: '⚙️', modules: [] },
        { name: 'Billing and Pricing', description: 'Pricing models, account structures, and billing support.', icon: '💳', modules: [] }
      ]
    },
    {
      code: 'AIF-C01', name: 'AI Practitioner', category: 'Foundational',
      image: '/badges/aif-badge.png', color: 'bg-zinc-500', icon: '✨',
      domains: [
        {
          name: 'Fundamentals of AI & ML', description: 'Core concepts, terminologies, and use cases of Artificial Intelligence.', icon: '🧠',
          modules: [
            { id: 'aif1-1', title: 'What is Machine Learning?', duration: '8:45', type: 'video', videoUrl: 'https://www.youtube.com/embed/HcqpanDadyQ?autoplay=1' },
            { id: 'aif1-2', title: 'Supervised vs Unsupervised Learning', duration: '12:20', type: 'video', videoUrl: 'https://www.youtube.com/embed/PeM1hOcFa4A?autoplay=1' },
            {
              id: 'aif1-3',
              title: 'Core Terminology Cheatsheet',
              duration: 'Read',
              type: 'document',
              // 👇 ADD THIS CONTENT STRING (Use \n for line breaks!)
              content: "Artificial Intelligence (AI)\nThe simulation of human intelligence processes by machines, especially computer systems.\n\nMachine Learning (ML)\nA subset of AI that enables systems to learn and improve from experience without being explicitly programmed.\n\nDeep Learning (DL)\nA subset of ML based on artificial neural networks with multiple layers (hence 'deep').\n\nFoundation Models (FMs)\nLarge-scale machine learning models trained on vast amounts of unlabelled data, which can be adapted to a wide range of downstream tasks."
            },
            { id: 'aif1-4', title: 'Neural Networks Explained', duration: '15:10', type: 'video', videoUrl: 'https://www.youtube.com/embed/bfmFfD2RIcg?autoplay=1' }
          ]
        },
        {
          name: 'Fundamentals of Generative AI', description: 'Understanding Foundation Models, tokens, and prompt engineering.', icon: '🎨',
          modules: [
            { id: 'aif2-1', title: 'Introduction to LLMs', duration: '10:05', type: 'video', videoUrl: 'https://www.youtube.com/embed/zjkBMFhNj_g?autoplay=1' },
            { id: 'aif2-2', title: 'The Anatomy of a Token', duration: '7:30', type: 'video', videoUrl: 'https://www.youtube.com/embed/f9nAjpKDE0g?autoplay=1' },
            { id: 'aif2-3', title: 'Prompt Engineering Masterclass', duration: '22:15', type: 'video', videoUrl: 'https://www.youtube.com/embed/jC4v5AS4RIM?autoplay=1' }
          ]
        },
        {
          name: 'Applications of Foundation Models', description: 'Selecting and utilizing AWS AI services for specific business needs.', icon: '🚀',
          modules: [
            { id: 'aif3-1', title: 'Amazon Bedrock Overview', duration: '14:20', type: 'video', videoUrl: 'https://www.youtube.com/embed/ab1ebJv8Q0A?autoplay=1' },
            { id: 'aif3-2', title: 'Retrieval-Augmented Generation (RAG)', duration: '18:50', type: 'video', videoUrl: 'https://www.youtube.com/embed/T-D1OfcDW1M?autoplay=1' },
            { id: 'aif3-3', title: 'Choosing the Right Foundation Model', duration: 'Read', type: 'document' }
          ]
        },
        {
          name: 'Responsible AI & Security', description: 'Governance, bias mitigation, and secure AI implementation.', icon: '⚖️',
          modules: [
            { id: 'aif4-1', title: 'Identifying and Mitigating Bias', duration: '11:40', type: 'video', videoUrl: 'https://www.youtube.com/embed/EwjrE1M1r2A?autoplay=1' },
            { id: 'aif4-2', title: 'Data Privacy in Generative AI', duration: '9:15', type: 'video', videoUrl: 'https://www.youtube.com/embed/z57Xyv6p6nE?autoplay=1' },
            { id: 'aif4-3', title: 'Security Best Practices Checklist', duration: 'Read', type: 'document' }
          ]
        }
      ]
    },

    // --- ASSOCIATE ---
    {
      code: 'SAA-C03', name: 'Solutions Architect', category: 'Associate',
      image: '/badges/saa-badge.png', color: 'bg-blue-600', icon: '🏗️',
      domains: [
        { name: 'Secure Architectures', description: 'Design secure access, data protection, and robust security tiers.', icon: '🔐', modules: [] },
        { name: 'Resilient Architectures', description: 'Design scalable, fault-tolerant, and highly available systems.', icon: '🔄', modules: [] },
        { name: 'High-Performing Architectures', description: 'Optimize compute, storage, networking, and database solutions.', icon: '⚡', modules: [] },
        { name: 'Cost-Optimized Architectures', description: 'Design cost-effective compute, storage, and network architectures.', icon: '💰', modules: [] }
      ]
    },
    {
      code: 'DVA-C02', name: 'Developer Associate', category: 'Associate',
      image: '/badges/dva-badge.png', color: 'bg-blue-500', icon: '💻',
      domains: [
        { name: 'Development with AWS', description: 'Code for serverless applications, APIs, and AWS integration.', icon: '👨‍💻', modules: [] },
        { name: 'Security', description: 'Implement application security, encryption, and IAM authentication.', icon: '🔑', modules: [] },
        { name: 'Deployment', description: 'Deploy applications using CI/CD pipelines and AWS deployment tools.', icon: '🚢', modules: [] },
        { name: 'Troubleshooting & Optimization', description: 'Monitor, trace, and optimize code performance on AWS.', icon: '🔧', modules: [] }
      ]
    },
    {
      code: 'SOA-C02', name: 'SysOps Admin', category: 'Associate',
      image: '/badges/soa-badge.png', color: 'bg-blue-400', icon: '⚙️',
      domains: [
        { name: 'Monitoring & Logging', description: 'Implement metrics, alarms, and remediation strategies.', icon: '📈', modules: [] },
        { name: 'Reliability & Continuity', description: 'Implement scalable environments and disaster recovery.', icon: '🏥', modules: [] },
        { name: 'Deployment & Provisioning', description: 'Automate infrastructure using CloudFormation and Systems Manager.', icon: '🏗️', modules: [] },
        { name: 'Security & Compliance', description: 'Implement and manage security policies and compliance frameworks.', icon: '📋', modules: [] }
      ]
    },
    {
      code: 'DEA-C01', name: 'Data Engineering', category: 'Associate',
      image: '/badges/dea-badge.png', color: 'bg-teal-500', icon: '🗄️',
      domains: [
        { name: 'Data Ingestion & Transformation', description: 'Design data pipelines, ETL processes, and stream processing.', icon: '🌊', modules: [] },
        { name: 'Data Store Management', description: 'Manage operational databases, data warehouses, and data lakes.', icon: '💾', modules: [] },
        { name: 'Data Operations & Support', description: 'Automate data processing and maintain pipeline reliability.', icon: '⚙️', modules: [] },
        { name: 'Data Security & Governance', description: 'Implement data encryption, auditing, and access controls.', icon: '🔒', modules: [] }
      ]
    },

    // --- PROFESSIONAL ---
    {
      code: 'AIP-C01', name: 'Generative AI Developer', category: 'Professional',
      image: '/badges/aip-badge.png', color: 'bg-cyan-500', icon: '🧠',
      domains: [
        { name: 'Gen AI Fundamentals', description: 'Advanced understanding of LLMs, fine-tuning, and RAG architectures.', icon: '📚', modules: [] },
        { name: 'Implementation & Operations', description: 'Deploying, monitoring, and scaling Gen AI models in production.', icon: '🏭', modules: [] },
        { name: 'Security & Governance', description: 'Managing model toxicity, compliance, and secure data handling.', icon: '🛡️', modules: [] }
      ]
    },
    {
      code: 'SAP-C02', name: 'Solutions Architect Pro', category: 'Professional',
      image: '/badges/sap-badge.png', color: 'bg-indigo-700', icon: '🏛️',
      domains: [
        { name: 'Organizational Complexity', description: 'Design multi-account environments and complex network topologies.', icon: '🏢', modules: [] },
        { name: 'New Solutions', description: 'Architect massive, highly scalable, and specialized enterprise solutions.', icon: '✨', modules: [] },
        { name: 'Migration Planning', description: 'Plan large-scale enterprise migrations to the AWS Cloud.', icon: '🚚', modules: [] },
        { name: 'Cost Control & Improvement', description: 'Implement advanced enterprise cost allocation and optimization.', icon: '📉', modules: [] }
      ]
    },
    {
      code: 'DOP-C02', name: 'DevOps Engineer Pro', category: 'Professional',
      image: '/badges/dop-badge.png', color: 'bg-indigo-600', icon: '♾️',
      domains: [
        { name: 'SDLC Automation', description: 'Automate complex software delivery lifecycles and CI/CD pipelines.', icon: '⚙️', modules: [] },
        { name: 'Configuration & IaC', description: 'Manage complex infrastructure as code and configuration management.', icon: '📜', modules: [] },
        { name: 'Incident & Event Response', description: 'Design automated incident routing and remediation systems.', icon: '🚨', modules: [] },
        { name: 'High Availability & DR', description: 'Implement multi-region architectures and automated disaster recovery.', icon: '🌍', modules: [] }
      ]
    },

    // --- SPECIALTY ---
    {
      code: 'MLS-C01', name: 'Machine Learning', category: 'Specialty',
      image: '/badges/mls-badge.png', color: 'bg-purple-600', icon: '🤖',
      domains: [
        {
          name: 'Data Engineering', description: 'Data ingestion, transformation, and feature engineering for ML.', icon: '🗄️',
          modules: [
            { id: 'mls1-1', title: 'Building S3 Data Lakes', duration: '14:00', type: 'video', videoUrl: 'https://www.youtube.com/embed/k1RI5locZE4?autoplay=1' },
            { id: 'mls1-2', title: 'ETL Pipelines with AWS Glue', duration: '20:45', type: 'video', videoUrl: 'https://www.youtube.com/embed/5T2k2n0P3q8?autoplay=1' },
            { id: 'mls1-3', title: 'Amazon Kinesis Data Streams', duration: '18:10', type: 'video', videoUrl: 'https://www.youtube.com/embed/1c18L3kK-0o?autoplay=1' }
          ]
        },
        {
          name: 'Exploratory Data Analysis', description: 'Sanitizing, preparing, and visualizing data for modeling.', icon: '📊',
          modules: [
            { id: 'mls2-1', title: 'Handling Missing Data & Outliers', duration: '12:30', type: 'video', videoUrl: 'https://www.youtube.com/embed/8b512c-n-X8?autoplay=1' },
            { id: 'mls2-2', title: 'Data Visualization with QuickSight', duration: '15:20', type: 'video', videoUrl: 'https://www.youtube.com/embed/K81l-R1Xl7A?autoplay=1' },
            { id: 'mls2-3', title: 'Feature Engineering Techniques', duration: 'Read', type: 'document' }
          ]
        },
        {
          name: 'Modeling', description: 'Framing problems, selecting algorithms, and training ML models.', icon: '🧠',
          modules: [
            { id: 'mls3-1', title: 'Built-in SageMaker Algorithms', duration: '25:00', type: 'video', videoUrl: 'https://www.youtube.com/embed/YpQ_M-K535U?autoplay=1' },
            { id: 'mls3-2', title: 'Hyperparameter Tuning Strategies', duration: '16:40', type: 'video', videoUrl: 'https://www.youtube.com/embed/u1Q_q0XfJ2M?autoplay=1' },
            { id: 'mls3-3', title: 'Evaluating Model Performance Metrics', duration: '19:15', type: 'video', videoUrl: 'https://www.youtube.com/embed/p1I2bI2d9Z4?autoplay=1' }
          ]
        },
        {
          name: 'ML Implementation & Ops', description: 'Deploying, monitoring, and maintaining models in production.', icon: '🚀',
          modules: [
            { id: 'mls4-1', title: 'Deploying SageMaker Endpoints', duration: '14:50', type: 'video', videoUrl: 'https://www.youtube.com/embed/3K3qXv9Pz0s?autoplay=1' },
            { id: 'mls4-2', title: 'Model Monitor and Drift Detection', duration: '17:35', type: 'video', videoUrl: 'https://www.youtube.com/embed/z1X_u6I0D4w?autoplay=1' },
            { id: 'mls4-3', title: 'Security and IAM in SageMaker', duration: 'Read', type: 'document' }
          ]
        }
      ]
    },
    {
      code: 'SCS-C02', name: 'Security Specialty', category: 'Specialty',
      image: '/badges/scs-badge.png', color: 'bg-red-600', icon: '🛡️',
      domains: [
        { name: 'Threat Protection & IR', description: 'Implement threat detection, response, and remediation.', icon: '🎯', modules: [] },
        { name: 'Security Monitoring', description: 'Design and implement advanced security logging and monitoring.', icon: '👁️', modules: [] },
        { name: 'Identity & Access Management', description: 'Design complex, cross-account IAM and permission boundaries.', icon: '🔑', modules: [] },
        { name: 'Data Protection', description: 'Implement advanced encryption, key management, and data privacy.', icon: '💎', modules: [] }
      ]
    },
    {
      code: 'ANS-C01', name: 'Advanced Networking', category: 'Specialty',
      image: '/badges/ans-badge.png', color: 'bg-purple-500', icon: '🌐',
      domains: [
        { name: 'Network Design', description: 'Design complex hybrid and cloud-native network architectures.', icon: '📐', modules: [] },
        { name: 'Network Implementation', description: 'Implement routing, Direct Connect, and Transit Gateway solutions.', icon: '🔌', modules: [] },
        { name: 'Network Management', description: 'Operate and maintain complex AWS network infrastructure.', icon: '🎛️', modules: [] },
        { name: 'Security & Troubleshooting', description: 'Implement network security controls and troubleshoot connectivity.', icon: '🔍', modules: [] }
      ]
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

    // 1. First, try to count the answers from the database (checking BOTH naming conventions)
    const correctOptions = question.value.options.filter(o =>
        o.isCorrect === true || String(o.isCorrect).toLowerCase() === 'true' ||
        o.correct === true || String(o.correct).toLowerCase() === 'true'
    );

    let maxAllowed = 1;

    // 2. Set maxAllowed based on the database...
    if (correctOptions.length > 1) {
      maxAllowed = correctOptions.length;
    }
    // 3. THE FAILSAFE: If the database hid the answers, read the text just like the UI does!
    else if (isMultipleAnswer(question.value)) {
      const text = question.value.questionText;
      if (text.includes("THREE") || text.includes("Choose 3") || text.includes("Select 3")) {
        maxAllowed = 3;
      } else {
        maxAllowed = 2; // Default for AWS multi-select is 2
      }
    }

    // 4. The Selection Logic
    if (maxAllowed === 1) {
      // Single choice: just replace the array
      selectedAnswers.value = [optionId];
    } else {
      // Multi-choice: Toggle logic
      const index = selectedAnswers.value.indexOf(optionId);
      if (index > -1) {
        // If they clicked it again, uncheck it!
        selectedAnswers.value.splice(index, 1);
      } else if (selectedAnswers.value.length < maxAllowed) {
        // If they haven't hit the limit yet, check it!
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

  const abortQuiz = () => {
    const wantsToLeave = confirm("⚠️ ABORT SIMULATION?\n\nAre you sure you want to exit? Your progress will be wiped, and no score or XP will be recorded.");

    if (wantsToLeave) {
      // 1. Kill the clocks
      stopTimer();
      if (amrapInterval) clearInterval(amrapInterval);

      // 2. Erase the auto-save memory so they don't resume it on refresh
      localStorage.removeItem('aws_active_exam');

      // 3. Wipe the board and send them to the lobby
      resetSession();
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

// 📄 DOCUMENT EXPORT ENGINE
const downloadDocumentPDF = () => {
    if (!activeModule.value || activeModule.value.type !== 'document') return;

    const doc = new jsPDF();

    // 1. Draw the Header
    doc.setFontSize(22);
    doc.setFont("helvetica", "bold");
    doc.setTextColor(15, 23, 42);
    doc.text(activeModule.value.title, 14, 20);

    // Draw a sleek line under the title
    doc.setDrawColor(226, 232, 240);
    doc.line(14, 24, 196, 24);

    // 2. Draw the Content
    doc.setFontSize(11);
    doc.setFont("helvetica", "normal");
    doc.setTextColor(51, 65, 85);

    const contentText = activeModule.value.content || "No content available.";

    // Tell jsPDF to automatically wrap the text so it doesn't run off the page!
    const splitText = doc.splitTextToSize(contentText, 180);
    doc.text(splitText, 14, 35);

    // 3. Trigger the Download
    // Replaces spaces in the title with underscores for a clean filename
    doc.save(`AWS_Hub_${activeModule.value.title.replace(/\s+/g, '_')}.pdf`);
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

// 5. BULK UPLOAD: The CSV Data Pipeline
const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // We have to package the file into a special FormData envelope
  const formData = new FormData();
  formData.append('file', file);

  try {
    const token = localStorage.getItem('aws_jwt');

    // Alert the user that the upload is starting
    alert(`Initiating Data Pipeline for: ${file.name}...`);

    const response = await fetch(`${API_BASE_URL}/api/admin/questions/upload`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    });

    if (response.ok) {
      const result = await response.json();
      alert(`✅ MAINFRAME UPDATED: Successfully injected ${result.count} new questions!`);

      // Refresh the table so the new questions appear instantly!
      await fetchAdminQuestions();

      // Clear the file input so you can upload another one if needed
      event.target.value = '';
    } else {
      alert("❌ Matrix Error: Upload failed.");
    }
  } catch (error) {
    console.error("Pipeline severed:", error);
    alert("❌ Critical Error: Could not reach the server.");
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

// --- AUTHENTICATION PIPELINE ---
const submitAuth = async () => {
  authError.value = '';

  try {
    // 1. Determine the endpoint (Login vs Register)
    const endpoint = isLoginMode.value ? '/api/auth/login' : '/api/auth/register';

    // Alert the user that the system is working
    console.log(`Initiating ${isLoginMode.value ? 'Login' : 'Registration'} Protocol...`);

    // 2. Fire the primary request
    const response = await fetch(`http://localhost:8080${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(authForm.value)
    });

    if (response.ok) {
      // --- THE NEW AUTO-LOGIN LOGIC ---
      if (!isLoginMode.value) {
        // If they just registered, immediately hit the login endpoint behind the scenes!
        const autoLoginResponse = await fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: authForm.value.username,
            password: authForm.value.password
          })
        });

        if (autoLoginResponse.ok) {
          const loginData = await autoLoginResponse.json();
          completeLoginProcess(loginData.token, authForm.value.username);
          return; // Exit the function, we are done!
        }
      } else {
        // If it was just a normal login, process it
        const data = await response.json();
        completeLoginProcess(data.token, authForm.value.username);
      }
    } else {
      // Handle incorrect passwords or taken usernames
      const errorText = await response.text();
      authError.value = `Matrix Error: ${errorText}`;
    }
  } catch (error) {
    console.error("Authentication pipeline severed:", error);
    authError.value = "Critical Error: Could not reach the authentication server.";
  }
};

// Helper function to keep our code clean (Add this right below submitAuth!)
const completeLoginProcess = (token, username) => {
  // Save the VIP Pass
  localStorage.setItem('aws_jwt', token);

  // Set the current user in Vue's memory
  currentUser.value = { username: username };

  // Close the modal
  showAuthModal.value = false;

  // Route them straight to the Landing Dashboard!
  currentView.value = 'landing';

  // Clear the form for security
  authForm.value = { username: '', email: '', password: '' };
};

// --- MONETIZATION: Stripe Checkout Pipeline ---
const upgradeToPremium = async () => {
    try {
      const token = localStorage.getItem('aws_jwt');

      // 🚨 1. THE GUEST TRAPDOOR FIX: Tell them to log in first!
      if (!token) {

        // Auto-open the signup modal for them!
        showAuthModal.value = true;
        isLoginMode.value = false;
        return;
      }

      const response = await fetch(`${API_BASE_URL}/api/payment/checkout`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('aws_awaiting_stripe_return', 'true');
        localStorage.setItem('aws_return_view', currentView.value);
        if (selectedCert.value) {
          localStorage.setItem('aws_return_cert', JSON.stringify(selectedCert.value));
        }
        // THE WARP GATE: Sends them to Stripe!
        window.location.href = data.checkoutUrl;

      } else if (response.status === 400) {
        // 🚨 THE INTERCEPTOR: Java caught a double-charge!
        const errorData = await response.json();
        if (errorData.error === "User is already a Premium member.") {
          showAlreadyPremiumModal.value = true;
        } else {
          alert(errorData.error);
        }
      } else {
       }
    } catch (error) {
      // 🚨 2. THE OFFLINE FIX: Actually alert the user if the server is down!
      console.error("Payment pipeline severed:", error);

    }
  };

// 1. Opens the Paywall Modal (Attach this to your Knowledge Forge button!)
const triggerPremiumPaywall = () => {
    showPremiumModal.value = true;
  };

// 2. Handles the actual checkout process when they click "Sign Up" inside the modal
const handleSignUpClick = async () => {
    showUpgradePrompt.value = false; // Hide the prompt so it gets out of the way

    try {
      const token = localStorage.getItem('aws_jwt');

      // 🚨 THE GUEST TRAPDOOR: If no token, slide open the Auth modal
      if (!token) {
        localStorage.setItem('aws_pending_checkout', 'true');
        showAuthModal.value = true;
        isLoginMode.value = false; // Force it to the "Sign Up" tab
        return;
      }

      // They are logged in! Call your Java backend to get the Stripe URL
      const response = await fetch(`${API_BASE_URL}/api/payment/checkout`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
      });

      if (response.ok) {
        const data = await response.json();

        // Save their exact location in the app before they leave for Stripe
        localStorage.setItem('aws_awaiting_stripe_return', 'true');
        localStorage.setItem('aws_return_view', currentView.value);
        if (selectedCert.value) {
          localStorage.setItem('aws_return_cert', JSON.stringify(selectedCert.value));
        }

        // THE WARP GATE: Sends them to Stripe!
        window.location.href = data.checkoutUrl;

      } else if (response.status === 400) {
        // 🚨 THE INTERCEPTOR: Java caught a double-charge!
        const errorData = await response.json();
        if (errorData.error === "User is already a Premium member.") {
          showAlreadyPremiumModal.value = true; // Pop your "Already Premium" modal
        } else {
          console.error("Payment Error:", errorData.error);
        }
      } else {
        console.error("Matrix Error: Could not reach the payment gateway.");
      }

    } catch (error) {
      console.error("Payment pipeline severed:", error);
    }
  };

// 2. Fires when they pick a plan inside the Paywall
const initiateCheckout = async (planType) => {
    const token = localStorage.getItem('aws_jwt');

    // 🚨 The Seamless Guest Handoff
    if (!token) {
      showPremiumModal.value = false; // Hide the pricing
      showAuthModal.value = true;     // Show the signup
      isLoginMode.value = false;      // Force it to the "Register" tab
      return;
    }

    // NOTE: If you want to support both a $1 Trial AND a $10.99 Monthly plan,
    // you will eventually need to pass `planType` to your Java backend here
    // so it knows which Stripe Price ID to use. For now, we will route to your existing checkout.

    try {
      const response = await fetch(`${API_BASE_URL}/api/payment/checkout`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('aws_awaiting_stripe_return', 'true');
        localStorage.setItem('aws_return_view', currentView.value);
        if (selectedCert.value) {
          localStorage.setItem('aws_return_cert', JSON.stringify(selectedCert.value));
        }
        window.location.href = data.checkoutUrl; // Warp to Stripe!
      } else {
        const errorData = await response.json();
        alert(errorData.error || "Payment gateway error.");
      }
    } catch (error) {
      console.error("Payment pipeline severed:", error);
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
  // 🛠️ THE MASTER CERTIFICATE WATCHER
  // 🛠️ THE MASTER CERTIFICATE WATCHER (Memory Bank)
  // 🛠️ THE MASTER CERTIFICATE WATCHER (Memory Bank - FIXED)
  watch(selectedCert, (newCert, oldCert) => {

    // 1. If the selected cert becomes empty...
    if (!newCert) {
      // 🚨 THE FIX: Only delete memory if they actually had a cert selected a second ago.
      // If 'oldCert' is undefined, it means the page just booted up. DO NOT self-destruct!
      if (oldCert) {
        localStorage.removeItem('aws_active_cert');
      }
      return;
    }

    // 2. THE AUTO-SAVER: Save their choice to the browser's permanent hard drive
    localStorage.setItem('aws_active_cert', newCert.code);

    // 3. THE REPAIRMAN: Ensure the cert has its domains intact
    if (!newCert.domains) {
      const realCert = certifications.value.find(c => c.code === newCert.code);
      if (realCert && newCert !== realCert) {
        selectedCert.value = realCert;
        return;
      }
    }

    // 4. THE QUIZ LOADER (Your original logic)
    if (currentUser.value) {
      if (typeof fetchQuestion === 'function' && currentView.value === 'quiz') {
        fetchQuestion();
      }
    }
  }, { immediate: true });

  // 💾 LMS MEMORY BANK: Auto-save the user's place in the curriculum
  watch(activeDomain, (newDomain) => {
    if (newDomain) localStorage.setItem('aws_active_domain', newDomain.name);
    else localStorage.removeItem('aws_active_domain');
  });

  watch(activeModule, (newModule) => {
    // 🚨 THE FIX: Reset the player back to the thumbnail whenever they click a new lesson!
    isPlaying.value = false;

    if (newModule) localStorage.setItem('aws_active_module', newModule.id);
    else localStorage.removeItem('aws_active_module');
  });

  const handleDomainClick = (domain, index) => {
    const isPremiumUser = currentUser.value && currentUser.value.isPremium;

    // Domain 1 (index 0) is always free. Everything else requires Premium.
    if (index > 0 && !isPremiumUser) {
      if (typeof playSound === 'function') playSound('wrong');
      const wantsToUpgrade = confirm(`🔒 PREMIUM ACCESS REQUIRED\n\nDomain ${index + 1}: ${domain.name} is locked.\n\nWould you like to upgrade your account to unlock the full Knowledge Forge?`);

      if (wantsToUpgrade && typeof upgradeToPremium === 'function') {
        upgradeToPremium();
      }
      return; // Abort! Do not let them into the lesson screen.
    }

    // If they are Premium OR it's Domain 1, let them in!
    openDomainTraining(domain);
  };

  // 3. 🛠️ THE ROUTER: Opens the viewer and auto-loads the first lesson
  const openDomainTraining = (domain) => {
    if (typeof playSound === 'function') playSound('click');

    activeDomain.value = domain; // Save which domain they clicked

    // If this domain has modules, auto-load the very first one!
    if (domain.modules && domain.modules.length > 0) {
      activeModule.value = domain.modules[0];
    } else {
      activeModule.value = null; // Blank slate if no lessons exist yet
    }

    currentView.value = 'lessonViewer'; // Warp them to the video player!
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const isPlaying = ref(false); // Controls the video player state

  // --- 2. THE BOOT SEQUENCE ---
  onMounted(async () => {

    // 🚨 1. THE STRIPE INTERCEPTOR (Must run first!)
    const urlParams = new URLSearchParams(window.location.search);

    if (urlParams.get('success') === 'true') {
      const returnView = localStorage.getItem('aws_return_view');
      const returnCert = localStorage.getItem('aws_return_cert');

      if (returnView) currentView.value = returnView;
      if (returnCert) selectedCert.value = JSON.parse(returnCert);

      welcomeMessage.value = "Payment Secured! Premium Databanks Unlocked.";
      showSuccessHologram.value = true;

      // Clean up the URL (Removes the ?success=true)
      window.history.replaceState({}, document.title, window.location.pathname);

      // Erase the temporary Stripe memory
      localStorage.removeItem('aws_awaiting_stripe_return');
      localStorage.removeItem('aws_return_view');
      localStorage.removeItem('aws_return_cert');

    } else if (urlParams.get('canceled') === 'true') {
      alert("Payment was canceled. You have not been charged.");
      window.history.replaceState({}, document.title, window.location.pathname);
    } else if (localStorage.getItem('aws_awaiting_stripe_return') === 'true') {
      // Failsafe: Instant restore if the URL params were somehow missed
      const savedView = localStorage.getItem('aws_return_view');
      if (savedView) {
        currentView.value = savedView;
      }
    }

    // 2. Set the initial URL state so the browser back button works
    history.replaceState({ view: currentView.value }, '', `#${currentView.value}`);

    // 🚨 3. THE MEMORY RESTORE: Remember their certification BEFORE we load anything else!
    const savedCertCode = localStorage.getItem('aws_active_cert');
    if (savedCertCode && certifications.value) {
      const foundCert = certifications.value.find(c => c.code === savedCertCode);
      if (foundCert) {
        selectedCert.value = foundCert; // Restore the Certification

        // Restore the specific Domain (if they were in the Knowledge Forge)
        const savedDomainName = localStorage.getItem('aws_active_domain');
        if (savedDomainName && foundCert.domains) {
          const foundDomain = foundCert.domains.find(d => d.name === savedDomainName);
          if (foundDomain) {
            activeDomain.value = foundDomain;

            // Restore the exact Video/Doc Module they were reading
            const savedModuleId = localStorage.getItem('aws_active_module');
            if (savedModuleId && foundDomain.modules) {
              const foundModule = foundDomain.modules.find(m => m.id === savedModuleId);
              if (foundModule) {
                activeModule.value = foundModule;
              }
            }
          }
        }
      }
    }

    // 4. Always fetch global data for the dashboard immediately
    if (typeof fetchHallOfFame === 'function') {
      fetchHallOfFame();
    }
    if (typeof fetchLeaderboard === 'function') {
      fetchLeaderboard();
    }

    // 5. Check the browser's permanent memory for a returning user
    const savedUser = localStorage.getItem('aws_user');

    if (savedUser) {
      currentUser.value = JSON.parse(savedUser);
      showAuthModal.value = false; // Hide the login screen!

      // Fetch their personal history now that we know who they are
      if (typeof fetchHistory === 'function') {
        await fetchHistory();
      }

      // Load their lifetime stats into the Matrix Radar
      if (typeof fetchRadarStats === 'function') {
        await fetchRadarStats();
      }

      // Resume quiz if they refreshed the page while looking at a cert
      if (selectedCert.value && typeof fetchQuestion === 'function') {
        fetchQuestion();
      }
    }
  });

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

/* --- CYBERPUNK ACHIEVEMENT MODAL --- */
.cyber-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 10, 0, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.cyber-modal {
  position: relative;
  background: #050505;
  border: 2px solid #00ff00;
  box-shadow: 0 0 20px rgba(0, 255, 0, 0.4), inset 0 0 15px rgba(0, 255, 0, 0.2);
  padding: 3rem;
  text-align: center;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  font-family: 'Courier New', Courier, monospace;
}

/* Fake CRT Scanlines */
.scanlines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 4px, 3px 100%;
  pointer-events: none;
}

.cyber-header {
  color: #ff003c;
  font-size: 0.9rem;
  letter-spacing: 2px;
  margin-bottom: 1.5rem;
  animation: blink 2s infinite;
}

.cyber-title {
  color: #fff;
  font-size: 2.5rem;
  margin: 0;
  text-shadow: 2px 2px 0px #ff003c, -2px -2px 0px #00d8ff;
  letter-spacing: 4px;
}

.cyber-subtitle {
  color: #00ff00;
  font-size: 1.2rem;
  margin-top: 0.5rem;
  letter-spacing: 2px;
}

.xp-container {
  margin: 2rem 0;
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 5px;
}

.xp-plus { color: #00d8ff; font-size: 3rem; font-weight: bold; }
.xp-number { color: #00ff00; font-size: 5rem; font-weight: bold; text-shadow: 0 0 20px #00ff00; }
.xp-label { color: #00d8ff; font-size: 2rem; font-weight: bold; }

.cyber-user {
  color: #888;
  font-size: 1rem;
  margin-bottom: 2rem;
}

.neon-text {
  color: #fff;
  text-shadow: 0 0 5px #fff;
  font-weight: bold;
}

.cyber-btn {
  background: transparent;
  color: #00ff00;
  border: 1px solid #00ff00;
  padding: 1rem 3rem;
  font-size: 1.2rem;
  cursor: pointer;
  letter-spacing: 3px;
  text-transform: uppercase;
  transition: all 0.2s ease-in-out;
  position: relative;
  z-index: 2;
}

.cyber-btn:hover {
  background: #00ff00;
  color: #000;
  box-shadow: 0 0 15px #00ff00;
}

/* Animations */
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.glitch-fade-enter-active, .glitch-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.glitch-fade-enter-from, .glitch-fade-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(20px);
}

/* ========================================== */
/* 🛡️ ADMIN COMMAND CENTER STYLES 🛡️          */
/* ========================================== */

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem 2rem;
  background: #0f172a; /* slate-900 */
  border-radius: 1.5rem;
  border: 1px solid #1e293b;
}

.admin-header h2 {
  color: #38bdf8; /* sky-400 */
  font-family: monospace;
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: 0.1em;
  margin: 0;
}

.btn-create {
  background: #0ea5e9;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 0.75rem;
  font-weight: 900;
  font-family: monospace;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-create:hover { background: #0284c7; transform: translateY(-2px); }

/* The Data Table */
.admin-table {
  width: 100%;
  background: white;
  border-radius: 1.5rem;
  overflow: hidden;
  border-collapse: collapse;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.admin-table th {
  background: #f8fafc;
  padding: 1rem;
  text-align: left;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
  border-bottom: 2px solid #e2e8f0;
}

.admin-table td {
  padding: 1rem;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
  font-size: 0.875rem;
}

.admin-table tr:hover { background: #f8fafc; }

.truncate {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.badge {
  background: #eff6ff;
  color: #2563eb;
  padding: 0.25rem 0.5rem;
  border-radius: 0.5rem;
  font-weight: 800;
  font-size: 0.75rem;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.btn-edit {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #cbd5e1;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 0.75rem;
  cursor: pointer;
}
.btn-edit:hover { background: #e2e8f0; color: #0f172a; }

.btn-delete {
  background: #fef2f2;
  color: #ef4444;
  border: 1px solid #fecaca;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: bold;
  font-size: 0.75rem;
  cursor: pointer;
}
.btn-delete:hover { background: #fee2e2; color: #b91c1c; }

/* Modal Overlay */
.admin-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(15, 23, 42, 0.9);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.admin-modal-content {
  background: white;
  padding: 2.5rem;
  border-radius: 1.5rem;
  width: 90%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.admin-modal-content h3 {
  font-size: 1.5rem;
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 1rem;
  font-family: monospace;
}

.admin-modal-content label {
  font-size: 0.75rem;
  font-weight: 800;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.admin-modal-content input, .admin-modal-content textarea {
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 0.75rem;
  font-family: inherit;
  outline: none;
}
.admin-modal-content input:focus, .admin-modal-content textarea:focus {
  border-color: #38bdf8;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn-cancel {
  background: transparent;
  color: #64748b;
  border: none;
  font-weight: bold;
  cursor: pointer;
  padding: 0.75rem 1.5rem;
}
.btn-cancel:hover { color: #0f172a; }

.btn-save {
  background: #10b981;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 0.75rem;
  font-weight: 900;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-save:hover { background: #059669; }

</style>