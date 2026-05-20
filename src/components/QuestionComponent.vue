<template>

  <div class="min-h-screen bg-gray-50 pb-12 px-4 font-sans relative">

    <!-- 🔔 TOAST NOTIFICATION -->
    <Transition name="toast-slide">
      <div v-if="toastVisible" class="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] animate-pulse-once">        <div class="flex items-center gap-3 px-6 py-4 rounded-2xl shadow-2xl border backdrop-blur-md"
             :class="{
               'bg-red-950/90 border-red-500/50 text-red-200': toastType === 'error',
               'bg-amber-950/90 border-amber-500/50 text-amber-200': toastType === 'warning',
               'bg-emerald-950/90 border-emerald-500/50 text-emerald-200': toastType === 'success',
               'bg-indigo-950/90 border-indigo-500/50 text-indigo-200': toastType === 'info'
             }">
          <span class="text-xl">
            {{ toastType === 'error' ? '⚡' : toastType === 'warning' ? '⚠️' : toastType === 'success' ? '✅' : '📡' }}
          </span>
          <span class="text-sm font-bold tracking-wide">{{ toastMessage }}</span>
          <button @click="toastVisible = false" class="ml-2 opacity-60 hover:opacity-100 text-lg leading-none">&times;</button>
        </div>
      </div>
    </Transition>

    <!-- 🏆 FOUNDER DISCOUNT BANNER -->
    <Transition name="toast-slide">
      <div v-if="founderSpotsActive && currentView === 'landing'" class="fixed top-0 left-0 right-0 z-[9998] w-full">
        <div class="bg-gradient-to-r from-amber-500 via-orange-500 to-red-500 px-4 py-2 shadow-lg flex items-center justify-between gap-3 text-white">
          <div class="flex items-center gap-2 min-w-0">
            <span class="text-lg flex-shrink-0">🔥</span>
            <p class="text-xs font-bold truncate">Founding Member — <span class="font-black">{{ founderSpotsRemaining }}</span> spots left. 30% off monthly, 50% off annual. Forever.</p>
          </div>
          <button @click="showFounderBanner = false; showPremiumModal = true" class="bg-white text-orange-600 font-black text-[10px] uppercase tracking-widest px-3 py-1 rounded-lg hover:bg-orange-50 transition-colors whitespace-nowrap flex-shrink-0">
            Claim
          </button>
        </div>
      </div>
    </Transition>

    <!-- 📱 PWA INSTALL BUTTON (small, bottom-right) -->
    <button v-if="showPwaButton" @click="installPwa" class="fixed bottom-6 right-4 z-[9997] w-12 h-12 bg-indigo-600 hover:bg-indigo-500 text-white rounded-full shadow-lg flex items-center justify-center transition-all hover:scale-110" title="Install App">
      📲
    </button>

    <div v-if="showAuthModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-md flex items-center justify-center z-[200] p-4">
      <div class="bg-gray-900 border border-blue-500 rounded-2xl 2xl:rounded-[2rem] p-8 2xl:p-16 w-full max-w-md 2xl:max-w-2xl text-white shadow-[0_0_30px_rgba(59,130,246,0.3)] relative">
        <button @click="showAuthModal = false" class="absolute top-4 right-4 2xl:top-8 2xl:right-8 text-gray-400 hover:text-white text-3xl 2xl:text-5xl leading-none">&times;</button>
        <h2 class="text-3xl 2xl:text-5xl font-black text-center mb-6 2xl:mb-10 text-blue-400 tracking-tight">
          {{ isLoginMode ? 'Access Datapad' : 'Register Recruit' }}
        </h2>

        <div v-if="authError" class="bg-red-900/50 border border-red-500 text-red-200 p-3 2xl:p-5 rounded-xl mb-6 2xl:text-lg text-sm text-center font-bold animate-pulse">
          {{ authError }}
        </div>

        <form @submit.prevent="submitAuth">

          <div v-if="!isLoginMode" class="mb-4 2xl:mb-6 animate-fade-in">
            <label class="block text-xs 2xl:text-base font-black uppercase tracking-widest text-slate-400 mb-2 2xl:mb-3">
              Codename (Username)
            </label>
            <input
                type="text"
                v-model="authForm.username"
                placeholder="e.g. CloudNinja99"
                class="w-full bg-slate-800/50 border border-slate-700 text-white rounded-xl 2xl:rounded-2xl px-4 py-3 2xl:px-6 2xl:py-5 2xl:text-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                :required="!isLoginMode"
            />
          </div>

          <div class="mb-4 2xl:mb-6">
            <label class="block text-xs 2xl:text-base font-black uppercase tracking-widest text-slate-400 mb-2 2xl:mb-3">Email Address</label>
            <input
                type="email"
                v-model="authForm.email"
                placeholder="Email Address"
                class="w-full bg-slate-800/50 border border-slate-700 text-white rounded-xl 2xl:rounded-2xl px-4 py-3 2xl:px-6 2xl:py-5 2xl:text-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                required
            />
          </div>

          <div v-if="!isLoginMode" class="mb-4 2xl:mb-6 animate-fade-in">
            <input
                type="email"
                v-model="authForm.confirmEmail"
                placeholder="Confirm Email Address"
                class="w-full bg-slate-800/50 border border-slate-700 text-white rounded-xl 2xl:rounded-2xl px-4 py-3 2xl:px-6 2xl:py-5 2xl:text-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                :required="!isLoginMode"
            />
          </div>

          <div class="mb-4 2xl:mb-6">
            <label class="block text-xs 2xl:text-base font-black uppercase tracking-widest text-slate-400 mb-2 2xl:mb-3">Password</label>
            <input
                type="password"
                v-model="authForm.password"
                placeholder="Password"
                class="w-full bg-slate-800/50 border border-slate-700 text-white rounded-xl 2xl:rounded-2xl px-4 py-3 2xl:px-6 2xl:py-5 2xl:text-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                required
            />
          </div>

          <div v-if="!isLoginMode" class="mb-6 2xl:mb-8 animate-fade-in">
            <input
                type="password"
                v-model="authForm.confirmPassword"
                placeholder="Confirm Password"
                class="w-full bg-slate-800/50 border border-slate-700 text-white rounded-xl 2xl:rounded-2xl px-4 py-3 2xl:px-6 2xl:py-5 2xl:text-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                :required="!isLoginMode"
            />
          </div>

          <p v-if="authValidationMessage" class="text-red-400 font-bold text-center mb-4 text-sm animate-pulse">
            {{ authValidationMessage }}
          </p>

          <button
              type="submit"
              class="w-full py-4 2xl:py-6 bg-blue-600 hover:bg-blue-500 text-white font-black 2xl:text-2xl rounded-xl 2xl:rounded-2xl uppercase tracking-widest transition-all shadow-lg shadow-blue-600/30 mb-4"
          >
            {{ isLoginMode ? 'Login' : 'Create Account' }}
          </button>

        </form>

        <div class="text-center mt-6 2xl:mt-8">
          <button @click="isLoginMode = !isLoginMode; authError = ''" class="text-sm 2xl:text-lg font-bold text-slate-400 hover:text-white transition-colors">
            {{ isLoginMode ? 'Need an account? Register here.' : 'Already a recruit? Login here.' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showAgreementModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4 backdrop-blur-sm bg-slate-900/90 transition-all">
      <div class="bg-slate-900 w-full max-w-2xl 2xl:max-w-4xl rounded-3xl 2xl:rounded-[3rem] border border-slate-700 shadow-2xl shadow-indigo-900/20 overflow-hidden flex flex-col">

        <div class="p-6 2xl:p-10 border-b border-slate-800 flex justify-between items-center bg-slate-800/50">
          <h2 class="text-xl 2xl:text-3xl font-black text-white uppercase tracking-widest flex items-center gap-3">
            <span class="text-indigo-500">⚖️</span> End User License Agreement
          </h2>
          <button @click="showAgreementModal = false" class="text-slate-400 hover:text-white 2xl:text-3xl transition-colors">
            ✕
          </button>
        </div>

        <div class="p-6 2xl:p-10 bg-slate-900/50">
          <p class="text-sm 2xl:text-xl text-slate-400 mb-4 2xl:mb-8 font-bold">Please review the Knowledge Forge Academy subscription terms before initiating the secure uplink.</p>

          <div class="max-h-60 2xl:max-h-96 overflow-y-auto pr-4 space-y-4 2xl:space-y-6 text-xs 2xl:text-base text-slate-400 leading-relaxed custom-scrollbar border border-slate-800 p-4 2xl:p-8 rounded-xl 2xl:rounded-2xl bg-slate-950">
            <p><strong class="text-slate-300 uppercase block mb-1 2xl:mb-2 2xl:text-lg">1. Subscription Terms</strong>By clicking "I Agree", you authorize Knowledge Forge Academy to charge your payment method on a recurring monthly basis until you cancel. You may cancel at any time through your account portal.</p>
            <p><strong class="text-slate-300 uppercase block mb-1 2xl:mb-2 2xl:text-lg">2. No Refunds</strong>Because you gain immediate access to proprietary digital databanks, training modules, and architectural blueprints, all charges are final and non-refundable.</p>
            <p><strong class="text-slate-300 uppercase block mb-1 2xl:mb-2 2xl:text-lg">3. Usage Restrictions</strong>Your account is for personal use only. Sharing your login credentials, scraping the databanks, or attempting to reverse-engineer the Knowledge Forge mainframe will result in immediate termination without refund.</p>
            <p><strong class="text-slate-300 uppercase block mb-1 2xl:mb-2 2xl:text-lg">4. Uptime & Availability</strong>While we strive for 99.9% uptime, the Knowledge Forge makes no guarantees regarding uninterrupted access to the platform.</p>
          </div>
        </div>

        <div class="p-6 2xl:p-10 border-t border-slate-800 bg-slate-900 flex flex-col gap-4 2xl:gap-8">

          <label class="flex items-center gap-3 2xl:gap-5 cursor-pointer group">
            <div class="relative flex items-center justify-center w-6 h-6 2xl:w-10 2xl:h-10 rounded 2xl:rounded-md border-2 transition-colors"
                 :class="hasAcceptedTerms ? 'border-indigo-500 bg-indigo-500' : 'border-slate-600 bg-slate-800 group-hover:border-slate-500'">
              <input type="checkbox" v-model="hasAcceptedTerms" class="sr-only" />
              <svg v-if="hasAcceptedTerms" class="w-4 h-4 2xl:w-6 2xl:h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <span class="text-sm 2xl:text-xl font-bold text-slate-300 group-hover:text-white transition-colors">
            I have read and agree to the Subscription Terms of Service.
          </span>
          </label>

          <div class="flex justify-end gap-3 2xl:gap-6 mt-2">
            <button @click="showAgreementModal = false" class="px-6 py-3 2xl:px-10 2xl:py-5 rounded-xl 2xl:rounded-2xl font-bold text-sm 2xl:text-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors">
              Cancel
            </button>
            <button
                @click="proceedToStripe"
                :disabled="!hasAcceptedTerms"
                class="px-8 py-3 2xl:px-12 2xl:py-5 rounded-xl 2xl:rounded-2xl font-black text-sm 2xl:text-lg uppercase tracking-widest transition-all flex items-center gap-2"
                :class="hasAcceptedTerms ? 'bg-indigo-600 text-white hover:bg-indigo-500 shadow-lg shadow-indigo-500/20 hover:-translate-y-0.5' : 'bg-slate-800 text-slate-500 cursor-not-allowed'"
            >
              Proceed to Checkout <span>→</span>
            </button>
          </div>
        </div>

      </div>
    </div>

    <div class="max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto flex flex-col md:flex-row justify-between items-center gap-4 mb-6 bg-white p-6 md:p-4 2xl:p-8 rounded-3xl 2xl:rounded-[2.5rem] shadow-sm border border-slate-100 mt-6 transition-all duration-300">
      <div class="flex items-center gap-2 px-4 2xl:px-8 flex-shrink-0">

        <div
            @dblclick="triggerAdminLock"
            class="flex items-center gap-5 md:gap-6 2xl:gap-10 group select-none cursor-pointer"
        >

          <div class="flex-shrink-0 transition-transform hover:scale-110">
            <img
                src="/image/logo/anvil.png"
                alt="Knowledge Forge Academy Logo"
                class="h-12 md:h-14 2xl:h-24 w-auto rounded-xl 2xl:rounded-2xl shadow-md border border-indigo-200"
            />
          </div>

          <h2 class="text-xl md:text-3xl 2xl:text-5xl font-black text-slate-900 tracking-tight leading-tight mb-0 flex-shrink min-w-0">
            Knowledge <span class="text-indigo-600">Forge</span> <span class="text-[8px] md:text-xs 2xl:text-lg font-black uppercase tracking-widest text-slate-400 block mt-1 2xl:mt-0">Academy</span>
          </h2>

        </div>

      </div>


      <div v-if="currentUser" class="flex flex-col md:flex-row items-center gap-3 2xl:gap-8">

        <div class="flex items-center gap-2 2xl:gap-4 px-4 py-2.5 2xl:px-8 2xl:py-5 bg-slate-50 rounded-xl 2xl:rounded-2xl border border-slate-200 shadow-sm transition-all hover:shadow-md">
          <span class="text-2xl 2xl:text-4xl transition-all duration-500" :class="[streakDisplay.color, streakDisplay.aura]">{{ streakDisplay.icon }}</span>
          <div class="flex flex-col text-left">
            <span class="text-[10px] 2xl:text-sm font-black uppercase tracking-wider text-slate-400 leading-none mb-1 2xl:mb-2">Daily Streak</span>
            <span class="text-sm 2xl:text-2xl font-bold text-slate-800 leading-none">
          {{ userStreak }} Days <span class="text-[10px] 2xl:text-base font-normal italic opacity-75 ml-1 hidden lg:inline-block">({{ streakDisplay.label }})</span>
        </span>
          </div>
        </div>

        <div class="flex items-center justify-between gap-4 2xl:gap-8 mb-2 md:mb-0">
          <div class="flex flex-col">
            <span class="text-[10px] 2xl:text-sm font-black uppercase tracking-[0.2em] mb-0.5 2xl:mb-1" :class="userRank.color">{{ userRank.title }}</span>
            <span class="text-xl 2xl:text-3xl font-black text-slate-900 tracking-tighter leading-none drop-shadow-sm truncate max-w-[120px] 2xl:max-w-[200px]">{{ currentUser?.username }}</span>
          </div>
          <div class="h-8 2xl:h-12 w-px bg-slate-200 hidden md:block"></div>

          <button @click="logout" class="group relative flex flex-col items-center">
            <span class="text-[9px] 2xl:text-xs font-black text-red-500 uppercase tracking-widest transition-all group-hover:text-red-700">Logout</span>
            <span class="h-0.5 2xl:h-1 w-0 bg-red-500 transition-all group-hover:w-full mt-1"></span>
          </button>
        </div>

      </div>

      <div v-else class="flex items-center gap-2 md:gap-4 2xl:gap-8 px-2 md:px-4">
        <button @click="showAuthModal = true; isLoginMode = true; authError = ''" class="text-xs md:text-sm 2xl:text-xl font-bold text-slate-500 hover:text-blue-600 transition-colors">Log In</button>
        <button @click="showAuthModal = true; isLoginMode = false; authError = ''" class="bg-blue-600 hover:bg-blue-700 text-white text-xs md:text-sm 2xl:text-xl font-black px-4 py-2 md:px-6 md:py-2.5 2xl:px-10 2xl:py-4 rounded-lg md:rounded-xl 2xl:rounded-2xl shadow-md hover:shadow-lg transition-all hover:-translate-y-0.5">Sign Up</button>
      </div>
    </div>

    <div v-if="selectedCert && currentView === 'landing'" id="protocol-menu" class="mt-8 mb-16 2xl:mt-12 2xl:mb-24 bg-white rounded-[2rem] 2xl:rounded-[3rem] p-6 md:p-8 2xl:p-16 border border-slate-200 shadow-xl animate-fade-in-up flex flex-col md:flex-row gap-8 2xl:gap-16 max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto">
      <div class="flex-shrink-0 flex flex-col items-center justify-center bg-slate-50 p-8 2xl:p-12 rounded-[1.5rem] 2xl:rounded-[2.5rem] border border-slate-100 md:w-1/3">
        <div class="w-36 h-36 md:w-44 md:h-44 2xl:w-56 2xl:h-56 rounded-2xl 2xl:rounded-[2rem] shadow-lg flex items-center justify-center mb-4 2xl:mb-8 transform transition-transform hover:scale-105 overflow-hidden">
          <img v-if="selectedCert.image" :src="selectedCert.image" class="w-full h-full object-contain" />
          <div v-else :class="[selectedCert.color, 'w-full h-full flex items-center justify-center text-5xl 2xl:text-8xl text-white']">{{ selectedCert.icon }}</div>
        </div>
        <h3 class="text-xl 2xl:text-4xl font-black text-slate-900 text-center leading-tight mb-1 2xl:mb-4">{{ selectedCert.name }}</h3>
        <span class="text-xs 2xl:text-lg font-bold px-3 py-1 2xl:px-6 2xl:py-2 bg-slate-200 text-slate-600 rounded-full uppercase tracking-wider mb-6 2xl:mb-10">{{ selectedCert.code }}</span>
        <button @click="selectedCert = null" class="mt-auto w-full group relative overflow-hidden flex flex-col items-center justify-center bg-white hover:bg-slate-50 p-4 2xl:p-6 rounded-2xl 2xl:rounded-3xl transition-all border-2 border-slate-100 hover:border-slate-300 shadow-sm hover:shadow-md">

          <div class="flex items-center gap-2 text-sm 2xl:text-xl font-black text-slate-800 uppercase tracking-widest mb-1 relative z-10">
            <span class="group-hover:-translate-x-1.5 transition-transform text-slate-400 group-hover:text-slate-800 text-lg 2xl:text-2xl">←</span>
            Switch Certification
          </div>

          <span class="text-[10px] 2xl:text-sm font-bold text-slate-400 uppercase tracking-wider text-center relative z-10">
      Return to roster & view scores
    </span>

          <div class="absolute inset-0 bg-gradient-to-t from-slate-100/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
        </button>
      </div>

      <div class="flex-grow flex flex-col gap-3 2xl:gap-6">
        <h4 class="text-xs 2xl:text-xl font-black text-slate-400 uppercase tracking-widest mb-1 px-2">Select Training Protocol</h4>

        <button @click="initiateProtocol('standard')" class="relative overflow-hidden group flex items-center gap-4 2xl:gap-8 bg-white p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-blue-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] 2xl:text-sm font-black px-3 py-1 2xl:px-6 2xl:py-2 rounded-bl-xl 2xl:rounded-bl-2xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl 2xl:text-5xl bg-blue-50 text-blue-500 w-12 h-12 2xl:w-24 2xl:h-24 flex flex-shrink-0 items-center justify-center rounded-xl 2xl:rounded-2xl group-hover:scale-110 group-hover:bg-blue-500 group-hover:text-white transition-all">🏛️</span>
          <div class="pr-16 2xl:pr-24">
            <h5 class="text-sm 2xl:text-3xl font-bold text-slate-900 group-hover:text-blue-600 transition-colors">Simulated Exam</h5>
            <p class="text-[11px] 2xl:text-lg text-slate-500 font-medium leading-tight mt-0.5 2xl:mt-2">Standard assessment. Timed. Real exam conditions.</p>
          </div>
        </button>

        <button @click="initiateProtocol('amrap')" class="relative overflow-hidden group flex items-center gap-4 2xl:gap-8 bg-white p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-red-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] 2xl:text-sm font-black px-3 py-1 2xl:px-6 2xl:py-2 rounded-bl-xl 2xl:rounded-bl-2xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl 2xl:text-5xl bg-red-50 text-red-500 w-12 h-12 2xl:w-24 2xl:h-24 flex flex-shrink-0 items-center justify-center rounded-xl 2xl:rounded-2xl group-hover:scale-110 group-hover:bg-red-500 group-hover:text-white transition-all">⏱️</span>
          <div class="pr-16 2xl:pr-24">
            <h5 class="text-sm 2xl:text-3xl font-bold text-slate-900 group-hover:text-red-600 transition-colors">AMRAP Attack</h5>
            <p class="text-[11px] 2xl:text-lg text-slate-500 font-medium leading-tight mt-0.5 2xl:mt-2">10 minute clock. Infinite questions. Pure speed and recall.</p>
          </div>
        </button>

        <button @click="initiateProtocol('sudden_death')" class="relative overflow-hidden group flex items-center gap-4 2xl:gap-8 bg-white p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl border-2 border-purple-100 hover:border-purple-500 hover:shadow-xl hover:shadow-purple-500/20 hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-emerald-400 to-green-500 text-white text-[10px] 2xl:text-sm font-black px-4 py-1 2xl:px-6 2xl:py-2 rounded-bl-xl 2xl:rounded-bl-2xl tracking-widest uppercase shadow-[0_0_15px_rgba(16,185,129,0.6)] animate-pulse">
            ⚡ Free Play
          </div>
          <span class="text-2xl 2xl:text-5xl bg-purple-50 text-purple-500 w-12 h-12 2xl:w-24 2xl:h-24 flex flex-shrink-0 items-center justify-center rounded-xl 2xl:rounded-2xl group-hover:scale-110 group-hover:bg-purple-500 group-hover:text-white transition-all">💀</span>
          <div class="pr-20 2xl:pr-32">
            <h5 class="text-sm 2xl:text-3xl font-bold text-slate-900 group-hover:text-purple-600 transition-colors">Sudden Death</h5>
            <p class="text-[11px] 2xl:text-lg text-slate-500 font-medium leading-tight mt-0.5 2xl:mt-2">3 Lives. No timer. One mistake shatters a heart. Survive.</p>
          </div>
        </button>

        <button @click="initiateProtocol('flashcards')" class="relative overflow-hidden group flex items-center gap-4 2xl:gap-8 bg-white p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-pink-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[10px] 2xl:text-sm font-black px-3 py-1 2xl:px-6 2xl:py-2 rounded-bl-xl 2xl:rounded-bl-2xl tracking-widest uppercase shadow-[0_0_15px_rgba(245,158,11,0.5)]">
            ⭐ Premium
          </div>
          <span class="text-2xl 2xl:text-5xl bg-pink-50 text-pink-500 w-12 h-12 2xl:w-24 2xl:h-24 flex flex-shrink-0 items-center justify-center rounded-xl 2xl:rounded-2xl group-hover:scale-110 group-hover:bg-pink-500 group-hover:text-white transition-all">🎴</span>
          <div class="pr-16 2xl:pr-24">
            <h5 class="text-sm 2xl:text-3xl font-bold text-slate-900 group-hover:text-pink-600 transition-colors">Flashcard Matrix</h5>
            <p class="text-[11px] 2xl:text-lg text-slate-500 font-medium leading-tight mt-0.5 2xl:mt-2">Match terms to definitions. 6 pairs per round. Test your vocabulary.</p>
          </div>
        </button>

        <button @click="initiateProtocol('library')" class="relative overflow-hidden group flex items-center gap-4 2xl:gap-8 bg-white p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-emerald-500 hover:shadow-xl hover:-translate-y-0.5 transition-all text-left w-full">
          <div class="absolute top-0 right-0 bg-gradient-to-r from-emerald-400 to-green-500 text-white text-[10px] 2xl:text-sm font-black px-4 py-1 2xl:px-6 2xl:py-2 rounded-bl-xl 2xl:rounded-bl-2xl tracking-widest uppercase shadow-[0_0_15px_rgba(16,185,129,0.6)] animate-pulse">
            ⚡ Free Access
          </div>
          <span class="text-2xl 2xl:text-5xl bg-emerald-50 text-emerald-500 w-12 h-12 2xl:w-24 2xl:h-24 flex flex-shrink-0 items-center justify-center rounded-xl 2xl:rounded-2xl group-hover:scale-110 group-hover:bg-emerald-500 group-hover:text-white transition-all">🧠</span>
          <div class="pr-16 2xl:pr-24">
            <h5 class="text-sm 2xl:text-3xl font-bold text-slate-900 group-hover:text-emerald-600 transition-colors">The Knowledge Forge</h5>
            <p class="text-[11px] 2xl:text-lg text-slate-500 font-medium leading-tight mt-0.5 2xl:mt-2">Access study guides, architectural diagrams, and video modules.</p>
          </div>
        </button>
      </div>
    </div>

    <div v-if="currentView === 'onboarding'" class="min-h-[80vh] flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white max-w-2xl 2xl:max-w-5xl w-full rounded-[2.5rem] 2xl:rounded-[4rem] shadow-2xl border border-slate-200 overflow-hidden relative">

        <div class="flex h-2 2xl:h-4 w-full bg-slate-100">
          <div
              class="bg-indigo-500 h-full transition-all duration-500 ease-out"
              :style="{ width: ((activeSlide + 1) / onboardingSlides.length) * 100 + '%' }"
          ></div>
        </div>

        <div class="p-8 md:p-12 2xl:p-20">
          <div class="min-h-[250px] 2xl:min-h-[400px] flex flex-col items-center text-center animate-fade-in-up" :key="activeSlide">
            <div class="w-24 h-24 2xl:w-40 2xl:h-40 bg-indigo-50 text-5xl 2xl:text-7xl rounded-full flex items-center justify-center mb-8 2xl:mb-12 shadow-inner border border-indigo-100">
              {{ onboardingSlides[activeSlide].icon }}
            </div>
            <h2 class="text-3xl 2xl:text-6xl font-black text-slate-900 tracking-tight mb-4 2xl:mb-8">
              {{ onboardingSlides[activeSlide].title }}
            </h2>
            <p class="text-lg 2xl:text-2xl text-slate-500 font-medium leading-relaxed max-w-lg 2xl:max-w-3xl">
              {{ onboardingSlides[activeSlide].text }}
            </p>
          </div>

          <div class="flex items-center justify-between mt-12 2xl:mt-20 pt-8 2xl:pt-12 border-t border-slate-100">

            <div class="flex gap-2 2xl:gap-4">
              <button
                  v-for="(slide, index) in onboardingSlides"
                  :key="index"
                  @click="activeSlide = index"
                  class="h-2.5 2xl:h-4 rounded-full transition-all duration-300"
                  :class="activeSlide === index ? 'w-8 2xl:w-12 bg-indigo-600' : 'w-2.5 2xl:w-4 bg-slate-200 hover:bg-slate-300'"
              ></button>
            </div>

            <div class="flex gap-3 2xl:gap-6">
              <button
                  @click="prevSlide"
                  :class="activeSlide === 0 ? 'opacity-0 pointer-events-none' : 'opacity-100'"
                  class="px-6 py-3 2xl:px-10 2xl:py-5 rounded-xl 2xl:rounded-2xl font-bold text-sm 2xl:text-xl uppercase tracking-widest text-slate-500 bg-slate-100 hover:bg-slate-200 transition-all"
              >
                Back
              </button>
              <button
                  @click="nextSlide"
                  class="px-8 py-3 2xl:px-12 2xl:py-5 rounded-xl 2xl:rounded-2xl font-black text-sm 2xl:text-xl uppercase tracking-widest text-white transition-all shadow-md hover:-translate-y-0.5"
                  :class="activeSlide === onboardingSlides.length - 1 ? 'bg-emerald-500 hover:bg-emerald-400 shadow-emerald-500/30' : 'bg-indigo-600 hover:bg-indigo-500 shadow-indigo-500/30'"
              >
                {{ activeSlide === onboardingSlides.length - 1 ? 'Enter the Forge' : 'Next' }}
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>

    <div v-if="currentView === 'landing'" class="max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 md:px-8 lg:px-12 transition-all duration-500">
      <div class="text-center mb-12 md:mb-20 2xl:mb-32 mt-8 md:mt-12 2xl:mt-20">
        <h1 class="text-4xl md:text-6xl lg:text-7xl 2xl:text-[7rem] font-extrabold text-gray-900 mb-4 md:mb-6 2xl:mb-10 tracking-tight">AWS Certification Hub</h1>
        <p class="text-lg md:text-2xl lg:text-3xl 2xl:text-5xl text-gray-600 font-medium">Select your path and master the cloud.</p>
      </div>

      <div v-for="(certs, categoryName) in groupedCerts" :key="categoryName">
        <div v-if="certs.length > 0" class="mb-12 2xl:mb-20">
          <div class="flex items-center gap-4 2xl:gap-8 mb-8 2xl:mb-12">
            <h2 class="text-2xl 2xl:text-4xl font-black text-slate-800 uppercase tracking-widest">{{ categoryName }}</h2>
            <div class="h-px bg-slate-200 flex-grow"></div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 2xl:gap-12">
            <div v-for="cert in certs" :key="cert.code" @click="selectCertification(cert)" class="group cursor-pointer bg-white rounded-[2.5rem] 2xl:rounded-[3.5rem] p-8 2xl:p-12 shadow-sm border border-slate-100 hover:shadow-2xl hover:-translate-y-2 transition-all flex flex-col items-center text-center">
              <div class="h-28 w-28 md:h-40 md:w-40 2xl:h-64 2xl:w-64 mb-6 2xl:mb-10 flex items-center justify-center relative">
                <img v-if="cert.image" :src="cert.image" @error="cert.image = null" class="w-full h-full object-contain drop-shadow-2xl group-hover:scale-110 transition-transform duration-300" />
                <div v-else :class="['w-full h-full rounded-3xl 2xl:rounded-[2.5rem] flex items-center justify-center text-5xl 2xl:text-8xl text-white shadow-lg', cert.color]">{{ cert.icon }}</div>
              </div>
              <div class="space-y-2 2xl:space-y-4">
                <h3 class="font-black text-slate-900 text-xl md:text-2xl 2xl:text-4xl leading-tight">{{ cert.name }}</h3>
                <p class="text-blue-600 text-[10px] md:text-xs 2xl:text-lg font-mono font-black uppercase tracking-[0.3em] opacity-60">{{ cert.code }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 2xl:gap-16">
        <div class="lg:col-span-2 bg-white rounded-3xl 2xl:rounded-[3rem] p-6 2xl:p-12 border border-gray-100 shadow-sm">
          <h2 class="text-2xl 2xl:text-4xl font-bold mb-6 2xl:mb-10">Recent Activity</h2>
          <div v-if="examHistory.length > 0">
            <div class="overflow-x-auto -mx-6 2xl:-mx-12">
              <div class="inline-block min-w-full align-middle px-6 2xl:px-12">
                <table class="min-w-[600px] w-full text-left border-collapse">
                  <thead class="bg-slate-50 border-b border-slate-100">
                  <tr>
                    <th class="px-6 py-4 2xl:px-8 2xl:py-6 text-xs 2xl:text-lg font-black uppercase tracking-widest text-slate-400 w-[30%]">Exam</th>
                    <th class="px-6 py-4 2xl:px-8 2xl:py-6 text-center text-xs 2xl:text-lg font-black uppercase tracking-widest text-slate-400 w-[15%]">Review</th>
                    <th class="px-6 py-4 2xl:px-8 2xl:py-6 text-center text-xs 2xl:text-lg font-black uppercase tracking-widest text-slate-400 w-[15%]">Score</th>
                    <th class="px-6 py-4 2xl:px-8 2xl:py-6 text-right text-xs 2xl:text-lg font-black uppercase tracking-widest text-slate-400 w-[25%]">Area</th>
                    <th class="px-6 py-4 2xl:px-8 2xl:py-6 w-[15%]"></th>
                  </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-50">
                  <tr v-for="attempt in examHistory" :key="attempt.id" class="hover:bg-slate-50/50 transition-colors">
                    <td class="px-6 py-4 2xl:px-8 2xl:py-6">
                      <div class="text-base 2xl:text-2xl font-bold text-slate-700 leading-tight">{{ attempt.examCode }}</div>
                      <div class="text-xs 2xl:text-base text-slate-400 mt-0.5 2xl:mt-2">{{ formatDate(attempt.completedAt) }}</div>
                    </td>
                    <td class="px-6 py-4 2xl:px-8 2xl:py-6 text-center">
                      <button @click="openReviewRoom(attempt.id)" class="text-xl 2xl:text-3xl">👁️</button>
                    </td>
                    <td class="px-6 py-4 2xl:px-8 2xl:py-6 text-center">
                      <span :class="['px-3 py-1 2xl:px-5 2xl:py-2 rounded-lg 2xl:rounded-xl font-black text-sm 2xl:text-xl border', attempt.scorePercentage >= 75 ? 'bg-green-50 text-green-600 border-green-100' : 'bg-red-50 text-red-600 border-red-100']">{{ attempt.scorePercentage }}%</span>
                    </td>
                    <td class="px-6 py-4 2xl:px-8 2xl:py-6 text-right">
                      <span class="bg-amber-50 text-amber-700 text-[10px] 2xl:text-sm font-black uppercase px-2 py-1 2xl:px-4 2xl:py-2 rounded-md 2xl:rounded-lg border border-amber-100">{{ attempt.weakestCategory || 'MIXED' }}</span>
                    </td>
                    <td class="px-6 py-4 2xl:px-8 2xl:py-6 text-center">
                      <button @click="deleteAttempt(attempt.id)" class="text-slate-400 hover:text-red-600 hover:scale-125 transition-all text-lg 2xl:text-2xl" title="Delete this attempt">🗑️</button>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-10 2xl:py-20 text-gray-400 2xl:text-2xl">Complete an exam to see your stats.</div>
        </div>

        <div class="bg-white rounded-[2rem] 2xl:rounded-[3rem] shadow-xl p-8 2xl:p-12 border border-slate-100 flex-1">
          <h3 class="text-2xl 2xl:text-4xl font-black text-slate-900 mb-6 2xl:mb-10 flex items-center gap-2 2xl:gap-4">✨ Ascended Masters</h3>
          <div v-if="leaderboardData.length > 0" class="space-y-3 2xl:space-y-6">
            <div v-for="(master, index) in leaderboardData" :key="index" class="flex items-center justify-between p-4 2xl:p-6 bg-slate-50 border border-slate-100 rounded-xl 2xl:rounded-2xl hover:shadow-md transition-all">
              <div class="flex items-center gap-4 2xl:gap-6">
                <div class="w-8 h-8 2xl:w-12 2xl:h-12 rounded-full flex items-center justify-center font-black text-sm 2xl:text-xl shadow-sm" :class="index === 0 ? 'bg-gradient-to-br from-amber-200 to-amber-400 text-amber-900' : index === 1 ? 'bg-gradient-to-br from-slate-200 to-slate-400 text-slate-800' : index === 2 ? 'bg-gradient-to-br from-orange-200 to-orange-400 text-orange-900' : 'bg-blue-100 text-blue-600'">#{{ index + 1 }}</div>
                <div>
                  <p class="font-black text-slate-800 2xl:text-2xl">{{ master.fullName }}</p>
                  <p class="text-[10px] 2xl:text-sm font-bold text-slate-400 tracking-widest uppercase 2xl:mt-1">{{ master.examCode }}</p>
                </div>
              </div>
              <div class="text-xl 2xl:text-4xl font-black" :class="master.scorePercentage === 100 ? 'text-emerald-500' : 'text-slate-700'">{{ master.scorePercentage }}%</div>
            </div>
          </div>
          <div v-else class="text-center py-10 2xl:py-20">
            <div class="text-5xl 2xl:text-7xl mb-4 2xl:mb-8 opacity-50 animate-pulse">☁️</div>
            <p class="text-slate-500 font-bold 2xl:text-2xl">No legends yet.</p>
            <p class="text-sm 2xl:text-xl text-slate-400 mt-1 2xl:mt-3">Score 100% to claim the top spot!</p>
          </div>
        </div>
      </div>

      <div class="mt-12 2xl:mt-24 grid grid-cols-1 lg:grid-cols-2 gap-8 2xl:gap-16">

        <div class="bg-white rounded-[2rem] 2xl:rounded-[3rem] p-8 2xl:p-12 shadow-sm border border-slate-100 flex flex-col h-full">
          <div class="mb-6 2xl:mb-10 flex justify-between items-start">
            <div>
              <h3 class="text-2xl 2xl:text-4xl font-black text-slate-800">Skill Radar</h3>
              <p class="text-slate-500 text-sm 2xl:text-xl mt-1 2xl:mt-2">Your mastery across architectural domains.</p>
            </div>
            <select
                v-model="selectedRadarCert"
                class="bg-slate-50 border border-slate-200 text-slate-700 text-xs md:text-sm 2xl:text-xl font-bold rounded-xl 2xl:rounded-2xl px-2 py-2 md:px-4 2xl:px-6 2xl:py-4 focus:outline-none focus:ring-2 focus:ring-indigo-500 cursor-pointer shadow-sm max-w-[160px] md:max-w-none"
            >
              <optgroup label="Foundational & Associate">
                <option value="CLF-C02">CLF-C02</option>
                <option value="AIF-C01">AIF-C01</option>
                <option value="SAA-C03">SAA-C03</option>
                <option value="DVA-C02">DVA-C02</option>
                <option value="SOA-C02">SOA-C02</option>
                <option value="DEA-C01">DEA-C01</option>
                <option value="AIP-C01">AIP-C01</option>
                <option value="MLA-C01">MLA-C01</option>
              </optgroup>
              <optgroup label="Professional & Specialty">
                <option value="SAP-C02">SAP-C02</option>
                <option value="DOP-C02">DOP-C02</option>
                <option value="SCS-C02">SCS-C02</option>
                <option value="ANS-C01">ANS-C01</option>
              </optgroup>
            </select>
          </div>
          <div class="flex-grow relative min-h-[300px] 2xl:min-h-[600px] w-full flex justify-center items-center">
            <Radar :key="chartViewCode" :data="dynamicRadarData" :options="radarOptions" />
          </div>
        </div>

        <div class="bg-white rounded-[2rem] 2xl:rounded-[3rem] p-8 2xl:p-12 shadow-sm border border-slate-100 flex flex-col h-full">
          <div class="mb-6 2xl:mb-10">
            <h3 class="text-2xl 2xl:text-4xl font-black text-slate-800">Target Priorities</h3>
            <p class="text-slate-500 text-sm 2xl:text-xl mt-1 2xl:mt-2">Recommended focus areas based on recent telemetry.</p>
          </div>
          <div class="flex-grow flex flex-col justify-center space-y-4 2xl:space-y-8">
            <div v-if="studyPriorities.length > 0" class="flex flex-col gap-3 2xl:gap-6">
              <div v-for="(priority, index) in studyPriorities" :key="priority.name" @click="jumpToForge(priority.name)" :class="index === 0 ? 'bg-red-50 border border-red-100 text-red-900 hover:bg-red-100' : 'bg-amber-50 border border-amber-100 text-amber-900 hover:bg-amber-100'" class="flex items-center justify-between p-4 2xl:p-8 rounded-xl 2xl:rounded-2xl shadow-sm transition-all hover:shadow-md cursor-pointer hover:-translate-y-1 group" title="Click to study this module in The Knowledge Forge">
                <div class="flex items-center gap-3 2xl:gap-6">
                  <span class="w-6 h-6 2xl:w-10 2xl:h-10 rounded-full flex items-center justify-center text-xs 2xl:text-lg font-black transition-transform group-hover:scale-110" :class="index === 0 ? 'bg-red-200 text-red-700' : 'bg-amber-200 text-amber-700'">!</span>
                  <span class="font-bold text-sm 2xl:text-2xl group-hover:underline">{{ priority.name }}</span>
                </div>
                <div class="flex items-center gap-4 2xl:gap-8">
                  <span class="font-black text-lg 2xl:text-4xl" :class="index === 0 ? 'text-red-600' : 'text-amber-600'">{{ priority.percentage }}%</span>
                  <span class="opacity-0 group-hover:opacity-100 transition-opacity font-bold text-xl 2xl:text-4xl" :class="index === 0 ? 'text-red-500' : 'text-amber-500'">➔</span>
                </div>
              </div>
            </div>
            <div v-else class="text-center p-6 2xl:p-12 text-slate-400 text-sm 2xl:text-xl font-bold">Insufficient telemetry data. Complete a simulation to generate priorities.</div>
          </div>
        </div>
      </div> <div v-if="recentReviews.length > 0" class="max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 md:px-8 lg:px-12 mt-24 mb-12 animate-fade-in">
      <div class="text-center mb-12">
        <h2 class="text-3xl md:text-5xl font-black text-slate-800 mb-4 tracking-tight">Forged in the Real World</h2>
        <p class="text-slate-500 font-medium text-lg md:text-xl">Join the engineers who passed the exam using our databanks.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div
            v-for="review in recentReviews"
            :key="review.id"
            class="bg-white rounded-3xl p-8 border border-slate-200 shadow-sm hover:shadow-xl hover:-translate-y-2 transition-all duration-300 relative"
        >
          <div class="absolute -top-6 -left-2 text-6xl text-blue-100 font-serif font-black select-none">"</div>

          <div class="flex gap-1 mb-4 relative z-10">
            <span v-for="n in review.rating" :key="n" class="text-amber-400 text-xl">⭐</span>
          </div>

          <p class="text-slate-700 font-medium leading-relaxed mb-6 relative z-10 italic">
            "{{ review.reviewText }}"
          </p>

          <div class="flex items-center gap-4 border-t border-slate-100 pt-6">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-black text-lg shadow-inner">
              {{ review.authorName.charAt(0).toUpperCase() }}
            </div>
            <div>
              <h4 class="font-black text-slate-900">{{ review.authorName }}</h4>
              <span class="text-xs font-bold uppercase tracking-widest text-emerald-500">{{ review.jobTitle || 'Cloud Ninja' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

      <!-- 📊 PERSONAL STATS (logged-in users) -->
      <div v-if="currentUser && userStats" class="max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto mt-16 2xl:mt-24 bg-white rounded-3xl 2xl:rounded-[3rem] p-8 md:p-10 2xl:p-16 border border-slate-200 shadow-sm">
        <h2 class="text-2xl 2xl:text-4xl font-black text-slate-900 mb-6 2xl:mb-10 flex items-center gap-3 2xl:gap-5">
          <span class="text-indigo-500">📊</span> Your Training Metrics
        </h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 2xl:gap-8">
          <div class="bg-gradient-to-br from-blue-50 to-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-blue-100 text-center">
            <div class="text-3xl 2xl:text-5xl font-black text-blue-600">{{ userStats.totalSessions || 0 }}</div>
            <div class="text-xs 2xl:text-sm font-bold text-slate-500 uppercase tracking-widest mt-1 2xl:mt-2">Games Played</div>
          </div>
          <div class="bg-gradient-to-br from-emerald-50 to-green-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-emerald-100 text-center">
            <div class="text-3xl 2xl:text-5xl font-black text-emerald-600">{{ userStats.studyMaterialViews || 0 }}</div>
            <div class="text-xs 2xl:text-sm font-bold text-slate-500 uppercase tracking-widest mt-1 2xl:mt-2">Study Views</div>
          </div>
          <div class="bg-gradient-to-br from-purple-50 to-violet-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-purple-100 text-center">
            <div class="text-3xl 2xl:text-5xl font-black text-purple-600">{{ userStats.topMode || '-' }}</div>
            <div class="text-xs 2xl:text-sm font-bold text-slate-500 uppercase tracking-widest mt-1 2xl:mt-2">Favorite Mode</div>
          </div>
          <div class="bg-gradient-to-br from-amber-50 to-orange-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-amber-100 text-center">
            <div class="text-3xl 2xl:text-5xl font-black text-amber-600">{{ userStats.topCert || '-' }}</div>
            <div class="text-xs 2xl:text-sm font-bold text-slate-500 uppercase tracking-widest mt-1 2xl:mt-2">Top Cert</div>
          </div>
        </div>
      </div>

      <!-- 🌐 AWS RESOURCES HUB -->
      <div class="max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto mt-16 2xl:mt-24 bg-white rounded-3xl 2xl:rounded-[3rem] p-8 md:p-10 2xl:p-16 border border-slate-200 shadow-sm">
        <h2 class="text-2xl 2xl:text-4xl font-black text-slate-900 mb-2 2xl:mb-4 flex items-center gap-3 2xl:gap-5">
          <span class="text-indigo-500">🌐</span> AWS Launch Pad
        </h2>
        <p class="text-sm 2xl:text-lg text-slate-500 font-medium mb-8 2xl:mb-12">Your gateway to the AWS ecosystem. Explore, learn, and certify.</p>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 2xl:gap-6">
          <a href="https://aws.amazon.com/certification/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">🎓</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Schedule Exam</span>
          </a>
          <a href="https://explore.skillbuilder.aws/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">📚</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Skill Builder</span>
          </a>
          <a href="https://aws.amazon.com/free/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">🆓</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Free Tier</span>
          </a>
          <a href="https://docs.aws.amazon.com/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">📖</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">AWS Docs</span>
          </a>
          <a href="https://aws.amazon.com/architecture/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">🏗️</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Architecture</span>
          </a>
          <a href="https://calculator.aws/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">🧮</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Pricing Calc</span>
          </a>
          <a href="https://aws.amazon.com/whitepapers/" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">📄</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">Whitepapers</span>
          </a>
          <a href="https://www.aboutamazon.com/news/aws" target="_blank" class="group bg-slate-50 hover:bg-indigo-50 p-5 2xl:p-8 rounded-2xl 2xl:rounded-3xl border border-slate-200 hover:border-indigo-300 transition-all text-center hover:-translate-y-1 hover:shadow-md">
            <span class="text-2xl 2xl:text-4xl block mb-2 2xl:mb-4 group-hover:scale-110 transition-transform">📰</span>
            <span class="text-xs 2xl:text-sm font-black text-slate-700 uppercase tracking-wider">AWS News</span>
          </a>
        </div>
      </div>

      <footer class="mt-24 2xl:mt-32 border-t border-slate-200 bg-slate-50/50 pt-12 pb-8 2xl:pt-20 2xl:pb-12 rounded-t-[3rem] 2xl:rounded-t-[4rem]">
        <div class="max-w-4xl 2xl:max-w-6xl mx-auto px-4 md:px-8 text-center flex flex-col items-center">

          <div class="mb-6 2xl:mb-8 transform hover:scale-105 transition-transform flex justify-center">
            <img
                src="/image/logo/anvil.png"
                alt="Knowledge Forge Academy"
                class="h-16 md:h-20 2xl:h-28 w-auto object-contain rounded-2xl shadow-lg border border-indigo-100"
            />
          </div>

          <h3 class="text-xl 2xl:text-4xl font-black text-slate-900 tracking-tight mb-1 2xl:mb-3">Knowledge Forge Academy</h3>
          <p class="text-sm 2xl:text-2xl font-medium text-slate-500 mb-6 2xl:mb-12 flex items-center gap-2 2xl:gap-4">
            <span>📍</span> Allen, TX 75002
          </p>

          <div class="flex flex-col sm:flex-row w-full gap-4 2xl:gap-8 justify-center">

            <a href="mailto:admin@knowledgeforgeacademy.com" class="inline-flex justify-center items-center gap-2 2xl:gap-5 bg-white text-indigo-600 hover:text-indigo-700 hover:bg-indigo-50 px-4 py-3 2xl:px-10 2xl:py-6 rounded-2xl 2xl:rounded-3xl font-black text-xs md:text-sm 2xl:text-xl uppercase tracking-widest transition-all border border-slate-200 hover:border-indigo-200 shadow-sm hover:shadow-md group flex-1 min-w-0">
              <span class="text-lg 2xl:text-3xl group-hover:animate-pulse flex-shrink-0">✉️</span>
              <span class="truncate">admin@knowledgeforgeacademy.com</span>
            </a>
          </div>

          <div class="flex justify-center gap-8 mt-10 font-bold text-slate-500 text-sm 2xl:text-base">
            <button @click="showAboutModal = true" class="hover:text-indigo-600 transition-colors">About Us</button>
            <button @click="showRatingModal = true" class="hover:text-indigo-600 transition-colors">Leave a Review</button>
            <button @click="showCancelModal = true" class="hover:text-indigo-600 transition-colors">Cancel Membership</button>
            <a href="#" class="hover:text-indigo-600 transition-colors">Terms of Service</a>
          </div>

          <div class="mt-8 2xl:mt-12 text-[10px] 2xl:text-sm font-black uppercase tracking-widest text-slate-400 border-t border-slate-200/60 pt-6 2xl:pt-10 w-full max-w-md 2xl:max-w-2xl">
            © 2026 Knowledge Forge Academy. System fully operational.
          </div>

        </div>
      </footer>

    </div>

    <div v-else-if="currentView === 'quiz'" class="max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 md:px-8 lg:px-12 transition-all duration-500">

      <div v-if="showResults && !isHistoricalView" class="bg-white rounded-[2rem] 2xl:rounded-[3rem] shadow-2xl p-10 2xl:p-20 text-center border border-gray-100">
        <div class="mb-10 2xl:mb-16 text-center flex flex-col items-center">
          <div class="mb-8 2xl:mb-12 flex justify-center w-full px-2">
            <img v-if="examResultMessage.image" :src="examResultMessage.image" class="w-full max-w-sm md:max-w-2xl 2xl:max-w-4xl h-auto object-cover rounded-2xl 2xl:rounded-3xl shadow-2xl border border-slate-100/50" alt="Result Animation" />
            <div v-else class="text-7xl md:text-8xl 2xl:text-[10rem]">{{ examResultMessage.icon }}</div>
          </div>
          <h1 :class="['text-3xl md:text-5xl 2xl:text-7xl font-black mb-4 2xl:mb-8 tracking-tight', examResultMessage.color]">{{ examResultMessage.title }}</h1>
          <div class="bg-slate-50 border border-slate-100 p-6 2xl:p-10 rounded-2xl 2xl:rounded-3xl max-w-2xl 2xl:max-w-4xl mx-auto shadow-inner">
            <p class="text-slate-600 font-mono text-sm md:text-base 2xl:text-2xl leading-relaxed italic">"{{ examResultMessage.text }}"</p>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4 2xl:gap-8 mb-8 2xl:mb-16 text-left">
          <div class="bg-blue-50 p-6 2xl:p-12 rounded-2xl 2xl:rounded-3xl text-center">
            <p class="text-blue-600 text-[10px] 2xl:text-lg font-black uppercase tracking-widest mb-1 2xl:mb-4">Score</p>
            <p class="text-4xl md:text-5xl 2xl:text-8xl font-black text-blue-900">{{ finalScore }}%</p>
          </div>
          <div class="bg-amber-50 p-6 2xl:p-12 rounded-2xl 2xl:rounded-3xl text-center flex flex-col justify-center border border-amber-100 shadow-inner">
            <div class="mb-3 2xl:mb-6">
              <p class="text-amber-600 text-[10px] 2xl:text-lg font-black uppercase tracking-widest mb-1 2xl:mb-2">Total Time</p>
              <p class="text-3xl 2xl:text-6xl font-black text-amber-900">{{ formattedTotalTime }}</p>
            </div>
            <div class="h-px bg-amber-200/50 w-full mb-3 2xl:mb-6"></div>
            <div>
              <p class="text-amber-600 text-[10px] 2xl:text-lg font-black uppercase tracking-widest mb-1 2xl:mb-2">Avg per Question</p>
              <p class="text-xl 2xl:text-4xl font-black text-amber-800">{{ formattedAverageTime }}</p>
            </div>
          </div>
        </div>
        <div class="flex flex-col gap-3 2xl:gap-6">
          <button @click="openReviewRoom(lastAttemptId)" class="bg-orange-500 hover:bg-amber-500 text-white py-4 2xl:py-8 rounded-2xl 2xl:rounded-3xl 2xl:text-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">🔍 Review My Answers</button>
          <button @click="handlePrint" class="bg-slate-800 hover:bg-slate-900 text-white py-4 2xl:py-8 rounded-2xl 2xl:rounded-3xl 2xl:text-2xl font-black shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">📄 Save Results as PDF</button>
          <button @click="resetSession" class="bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-indigo-600 text-white py-4 2xl:py-8 rounded-2xl 2xl:rounded-3xl 2xl:text-2xl font-black shadow-lg hover:shadow-xl hover:scale-[1.02] transition-all duration-300">Start New Session</button>
          <button @click="goBackToLanding" class="bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-slate-800 py-4 2xl:py-8 rounded-2xl 2xl:rounded-3xl 2xl:text-2xl font-bold transition-colors duration-300 border border-slate-200">Return to Dashboard</button>
        </div>
      </div>

      <div v-else-if="question && !showReviewModal" class="bg-white rounded-[2rem] 2xl:rounded-[3rem] shadow-xl overflow-hidden border border-slate-200 relative">
        <div v-if="isPaused" class="absolute inset-0 bg-white/95 backdrop-blur-md z-50 flex flex-col items-center justify-center text-center p-8 2xl:p-16">
          <div class="w-16 h-16 2xl:w-24 2xl:h-24 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center text-3xl 2xl:text-5xl mb-4 2xl:mb-8">⏸️</div>
          <h2 class="text-2xl 2xl:text-5xl font-black text-slate-800">Exam Paused</h2>
          <button @click="togglePause" class="mt-6 2xl:mt-10 bg-slate-800 text-white px-8 py-3 2xl:px-12 2xl:py-5 2xl:text-2xl rounded-2xl 2xl:rounded-3xl font-bold">Resume Exam</button>
        </div>

        <div class="bg-blue-600 p-4 md:p-6 2xl:p-10 flex flex-wrap justify-between items-center gap-2 text-white">
          <div>
            <p class="text-[10px] 2xl:text-base font-black uppercase tracking-widest opacity-70">{{ question.category }}</p>
            <h2 v-if="!isAmrapMode" class="text-sm md:text-lg 2xl:text-3xl font-bold">Question {{ sessionCount }} of {{ totalExamQuestions }}</h2>
            <h2 v-else class="text-sm md:text-lg 2xl:text-3xl font-bold">Reps: {{ amrapCorrectCount }}</h2>
          </div>
          <div class="flex items-center gap-2 2xl:gap-8 flex-wrap">
            <div v-if="isSuddenDeath" class="flex items-center gap-1.5 2xl:gap-3 bg-slate-900/40 px-3 py-1.5 2xl:px-6 2xl:py-3 rounded-xl 2xl:rounded-2xl shadow-inner border border-white/10">
              <span v-for="n in 3" :key="n" class="text-lg 2xl:text-3xl transition-all duration-500 ease-[cubic-bezier(0.34,1.56,0.64,1)]" :class="n <= healthPoints ? 'text-red-500 scale-100 drop-shadow-[0_0_8px_rgba(239,68,68,0.8)]' : 'opacity-20 scale-75 grayscale'">❤️</span>
            </div>
            <div v-else-if="isAmrapMode" class="flex items-center gap-2 2xl:gap-4 bg-red-600/20 px-3 py-1.5 2xl:px-8 2xl:py-3 rounded-xl 2xl:rounded-2xl border border-red-500/50 shadow-[0_0_15px_rgba(220,38,38,0.3)]">
              <span class="text-red-500 animate-pulse 2xl:text-3xl">⏱️</span>
              <span class="font-mono font-black text-red-100 tracking-widest text-sm md:text-lg 2xl:text-3xl">{{ formattedAmrapTime }}</span>
            </div>
            <span v-else class="font-mono font-bold text-sm 2xl:text-2xl bg-blue-500/50 px-3 py-1 2xl:px-6 2xl:py-2 rounded-lg 2xl:rounded-xl border border-blue-400/50">{{ formattedCurrentTime }}</span>
            <button @click="togglePause" class="bg-white text-blue-600 px-2 py-1 2xl:px-6 2xl:py-3 rounded-lg 2xl:rounded-xl text-[10px] md:text-xs 2xl:text-xl font-black uppercase tracking-widest shadow-lg">{{ isPaused ? 'Resume' : 'Pause' }}</button>
            <button @click="showGradeConfirmModal = true" class="bg-slate-900/40 hover:bg-slate-900/60 px-2 py-1 2xl:px-6 2xl:py-3 rounded-lg 2xl:rounded-xl text-[10px] md:text-xs 2xl:text-xl font-black uppercase tracking-widest shadow-lg transition-colors border border-white/10">Grade</button>
            <button @click="abortQuiz" class="bg-red-500/20 hover:bg-red-500/40 text-red-100 px-2 py-1 2xl:px-6 2xl:py-3 rounded-lg 2xl:rounded-xl text-[10px] md:text-xs 2xl:text-xl font-black uppercase tracking-widest shadow-lg transition-colors border border-red-400/30">Exit</button>
          </div>
        </div>

        <div class="p-8 2xl:p-16">
          <p class="text-xl md:text-3xl lg:text-3xl 2xl:text-5xl font-extrabold text-slate-800 mb-8 md:mb-12 2xl:mb-20 leading-relaxed md:leading-tight">{{ question.questionText }}</p>

          <div class="space-y-4 2xl:space-y-8">
            <button v-for="opt in question.options" :key="opt.id" @click="toggleAnswer(opt.id)" :class="['w-full text-left p-4 md:p-6 lg:p-8 2xl:p-12 rounded-2xl md:rounded-[2rem] 2xl:rounded-[3rem] border-2 transition-all flex items-center gap-4 md:gap-6 2xl:gap-10', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-50' : 'border-slate-100 bg-slate-50 hover:border-blue-200 hover:shadow-md']">
              <div class="w-6 h-6 2xl:w-10 2xl:h-10 border-2 flex items-center justify-center flex-shrink-0" :class="[isMultipleAnswer(question) ? 'rounded-md 2xl:rounded-lg' : 'rounded-full', selectedAnswers.includes(opt.id) ? 'border-blue-500 bg-blue-500 text-white' : 'border-slate-300 bg-white']">
                <span v-if="isMultipleAnswer(question) && selectedAnswers.includes(opt.id)" class="2xl:text-2xl">✓</span>
                <div v-if="!isMultipleAnswer(question) && selectedAnswers.includes(opt.id)" class="w-2.5 h-2.5 2xl:w-4 2xl:h-4 bg-white rounded-full"></div>
              </div>
              <span class="text-base md:text-lg lg:text-xl 2xl:text-3xl font-bold text-slate-700">{{ opt.text || opt.optionText || opt.value }}</span>
            </button>
          </div>

          <button v-if="!showExplanation" @click="checkAnswer" :disabled="selectedAnswers.length === 0" class="w-full mt-8 2xl:mt-16 py-4 2xl:py-8 rounded-2xl 2xl:rounded-[2rem] font-black 2xl:text-3xl text-white bg-blue-600 transition-all duration-300 ease-in-out hover:-translate-y-1 hover:shadow-[0_10px_40px_-10px_rgba(37,99,235,0.6)] active:translate-y-1 active:shadow-sm disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0">Submit Answer</button>

          <div v-if="showExplanation" class="mt-8 2xl:mt-16 animate-fade-in-up">
            <div :class="['p-4 2xl:p-8 rounded-2xl 2xl:rounded-3xl text-center font-black uppercase mb-4 2xl:mb-8 2xl:text-3xl', feedbackClass]">{{ feedback }}</div>
            <p class="bg-amber-50 p-5 2xl:p-10 rounded-2xl 2xl:rounded-3xl text-sm 2xl:text-2xl italic leading-relaxed border border-amber-100 text-slate-700">{{ question.explanation }}</p>

            <button @click="loadNextQuestion" class="w-full mt-4 2xl:mt-8 bg-green-600 text-white py-4 2xl:py-8 rounded-2xl 2xl:rounded-[2rem] font-black 2xl:text-3xl shadow-lg hover:shadow-xl hover:bg-green-500 transition-all">Next Question ➔</button>

            <!-- 🤖 AI TUTOR -->
            <div v-if="!isUserCorrect(question.id)" class="mt-4 2xl:mt-8">
              <button v-if="!aiTutorResponse && !aiTutorLoading" @click="askAiTutor" class="w-full py-3 2xl:py-6 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white rounded-2xl 2xl:rounded-[2rem] font-bold 2xl:text-2xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 2xl:gap-4">
                🤖 Still confused? Ask the AI Tutor
              </button>
              <div v-if="aiTutorLoading" class="bg-slate-900 rounded-2xl 2xl:rounded-[2rem] p-6 2xl:p-10 text-center">
                <div class="flex items-center justify-center gap-3 2xl:gap-5">
                  <div class="w-3 h-3 2xl:w-4 2xl:h-4 bg-purple-400 rounded-full animate-bounce"></div>
                  <div class="w-3 h-3 2xl:w-4 2xl:h-4 bg-indigo-400 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                  <div class="w-3 h-3 2xl:w-4 2xl:h-4 bg-blue-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                </div>
                <p class="text-purple-300 text-xs 2xl:text-base font-bold uppercase tracking-widest mt-3 2xl:mt-5 animate-pulse">AI Tutor is thinking...</p>
              </div>
              <div v-if="aiTutorResponse" class="bg-slate-900 rounded-2xl 2xl:rounded-[2rem] p-6 2xl:p-10 border border-purple-500/30 shadow-[0_0_20px_rgba(147,51,234,0.1)]">
                <div class="flex items-center gap-2 2xl:gap-4 mb-3 2xl:mb-6">
                  <span class="text-lg 2xl:text-2xl">🤖</span>
                  <span class="text-purple-400 text-xs 2xl:text-sm font-black uppercase tracking-widest">AI Tutor</span>
                </div>
                <p class="text-slate-200 text-sm 2xl:text-xl leading-relaxed whitespace-pre-line">{{ aiTutorResponse }}</p>
              </div>
            </div>

            <button @click="forceGradeExam" class="w-full mt-6 2xl:mt-8 bg-slate-100 text-slate-500 py-3 2xl:py-6 rounded-2xl 2xl:rounded-[2rem] font-bold 2xl:text-2xl border-2 border-dashed border-slate-200 hover:bg-slate-200 hover:text-slate-700 transition-all">🏁 Grade Now</button>
          </div>
        </div>
      </div>

    </div>

    <div v-else-if="currentView === 'flashcards'" class="max-w-5xl xl:max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 md:px-8 py-8 animate-fade-in transition-all duration-500">
      <div class="bg-white rounded-[2rem] shadow-xl border border-slate-200 overflow-hidden relative">

        <div class="bg-indigo-600 p-6 2xl:p-10 flex justify-between items-center text-white">
          <div>
            <p class="text-[10px] 2xl:text-sm font-black uppercase tracking-widest opacity-70">{{ selectedCert?.name || 'AWS' }}</p>
            <h2 class="text-lg md:text-xl 2xl:text-3xl font-bold">Flashcard Matrix - Round {{ currentFlashcardRound + 1 }}</h2>
          </div>
          <button @click="abortQuiz" class="bg-red-500/20 hover:bg-red-500/40 text-red-100 px-4 py-2 2xl:px-6 2xl:py-3 rounded-lg text-xs 2xl:text-base font-black uppercase tracking-widest shadow-lg transition-colors border border-red-400/30">🚪 Exit</button>
        </div>

        <div class="p-8 md:p-12 2xl:p-20 bg-slate-50 relative min-h-[500px] 2xl:min-h-[800px]">

          <div v-if="isFlashcardGameComplete" class="animate-fade-in-up max-w-3xl 2xl:max-w-5xl mx-auto">
            <div class="text-center mb-8 2xl:mb-12">
              <div class="text-6xl 2xl:text-8xl mb-4 2xl:mb-8">🏆</div>
              <h2 class="text-3xl 2xl:text-5xl font-black text-slate-900 tracking-tight">Matrix Cleared</h2>
              <p class="text-slate-500 2xl:text-xl font-medium mt-2 2xl:mt-4">You successfully matched all {{ allFetchedFlashcards.length }} terms.</p>
            </div>

            <div class="grid grid-cols-2 gap-4 2xl:gap-8 mb-8 2xl:mb-12">
              <div class="bg-indigo-50 border border-indigo-100 p-6 2xl:p-10 rounded-2xl text-center">
                <p class="text-[10px] 2xl:text-sm font-black uppercase tracking-widest text-indigo-400 mb-1 2xl:mb-3">Accuracy Score</p>
                <p class="text-4xl 2xl:text-6xl font-black text-indigo-600">{{ flashcardFinalScore }}%</p>
              </div>
              <div class="bg-emerald-50 border border-emerald-100 p-6 2xl:p-10 rounded-2xl text-center">
                <p class="text-[10px] 2xl:text-sm font-black uppercase tracking-widest text-emerald-400 mb-1 2xl:mb-3">Total Attempts</p>
                <p class="text-4xl 2xl:text-6xl font-black text-emerald-600">{{ flashcardAttempts }}</p>
              </div>
            </div>

            <div v-if="Object.keys(flashcardMisses).length > 0" class="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
              <div class="bg-slate-800 p-4 2xl:p-6">
                <h3 class="text-white font-black text-sm 2xl:text-lg uppercase tracking-widest flex items-center gap-2">
                  <span class="text-rose-500">⚠</span> Target Priorities (Terms Missed)
                </h3>
              </div>
              <div class="divide-y divide-slate-100 max-h-96 2xl:max-h-[600px] overflow-y-auto custom-scrollbar">
                <div v-for="miss in Object.values(flashcardMisses)" :key="miss.term" class="p-4 2xl:p-6 hover:bg-slate-50 transition-colors flex gap-4 2xl:gap-6">
                  <div class="w-8 h-8 2xl:w-12 2xl:h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center font-black text-xs 2xl:text-lg flex-shrink-0">
                    {{ miss.mistakes }}x
                  </div>
                  <div>
                    <p class="font-black text-slate-800 2xl:text-2xl">{{ miss.term }}</p>
                    <p class="text-sm 2xl:text-xl text-slate-500 mt-1 2xl:mt-3 leading-relaxed">{{ miss.definition }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="bg-emerald-50 border border-emerald-200 rounded-2xl p-8 2xl:p-12 text-center text-emerald-700 font-bold 2xl:text-2xl">
              Flawless Victory! You didn't make a single mistake.
            </div>

            <button @click="currentView = 'landing'" class="w-full mt-8 2xl:mt-12 bg-slate-800 hover:bg-slate-900 text-white py-4 2xl:py-6 2xl:text-xl rounded-xl font-black tracking-widest uppercase transition-all shadow-lg hover:shadow-xl hover:-translate-y-1">
              Return to Dashboard
            </button>
          </div>

          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-8 2xl:gap-16 relative">
            <div class="space-y-4 2xl:space-y-6">
              <h3 class="text-center text-xs md:text-sm 2xl:text-lg font-black text-slate-400 uppercase tracking-widest mb-4 2xl:mb-8">Terms</h3>
              <button
                  v-for="term in displayTerms" :key="'t-'+term.id"
                  @click="selectTerm(term)"
                  :disabled="matchedPairs.includes(term.id)"
                  class="w-full p-4 2xl:p-8 rounded-xl 2xl:rounded-2xl font-bold text-left transition-all duration-300 border-2 shadow-sm text-base md:text-lg 2xl:text-2xl"
                  :class="matchedPairs.includes(term.id) ? 'bg-emerald-50 border-emerald-200 text-emerald-400 opacity-50 scale-95' : selectedTerm?.id === term.id ? 'bg-indigo-600 border-indigo-700 text-white scale-105 shadow-lg' : 'bg-white border-slate-200 text-slate-700 hover:border-indigo-300 hover:shadow-md'"
              >
                {{ term.term }}
              </button>
            </div>

            <div class="space-y-4 2xl:space-y-6">
              <h3 class="text-center text-xs md:text-sm 2xl:text-lg font-black text-slate-400 uppercase tracking-widest mb-4 2xl:mb-8">Definitions</h3>
              <button
                  v-for="def in displayDefs" :key="'d-'+def.id"
                  @click="selectDef(def)"
                  :disabled="matchedPairs.includes(def.id)"
                  class="w-full p-4 2xl:p-8 rounded-xl 2xl:rounded-2xl text-sm md:text-base 2xl:text-xl font-medium text-left transition-all duration-300 border-2 shadow-sm leading-relaxed"
                  :class="matchedPairs.includes(def.id) ? 'bg-emerald-50 border-emerald-200 text-emerald-400 opacity-50 scale-95' : selectedDef?.id === def.id ? 'bg-pink-600 border-pink-700 text-white scale-105 shadow-lg' : 'bg-white border-slate-200 text-slate-600 hover:border-pink-300 hover:shadow-md'"
              >
                {{ def.definition }}
              </button>
            </div>
          </div>

          <div v-if="matchedPairs.length === 6 && displayTerms.length === 6 && !isFlashcardGameComplete" class="absolute inset-0 bg-white/90 backdrop-blur-sm z-50 flex flex-col items-center justify-center animate-fade-in rounded-b-[2rem]">
            <div class="text-6xl 2xl:text-9xl mb-4 2xl:mb-8 animate-bounce">✨</div>
            <h2 class="text-3xl 2xl:text-6xl font-black text-slate-900 mb-2 2xl:mb-4">Round Cleared!</h2>
            <p class="text-emerald-600 2xl:text-2xl font-mono tracking-widest uppercase font-black mb-8 2xl:mb-12">+100 XP Awarded</p>
            <button @click="currentFlashcardRound++; loadNextFlashcardRound()" class="bg-indigo-600 hover:bg-indigo-500 text-white px-8 py-4 2xl:px-12 2xl:py-6 rounded-2xl 2xl:rounded-3xl font-black 2xl:text-2xl uppercase tracking-widest shadow-lg shadow-indigo-500/30 transition-all hover:-translate-y-1">
              Next Round ➔
            </button>
          </div>

        </div>
      </div>
    </div>

    <div v-else-if="currentView === 'library'" class="max-w-7xl xl:max-w-[90rem] 2xl:max-w-[110rem] mx-auto px-4 md:px-8 py-6 animate-fade-in-up">

      <div class="mb-6 2xl:mb-10">
        <button @click="currentView = 'landing'" class="bg-slate-100 hover:bg-slate-200 text-slate-600 px-5 py-2.5 2xl:px-8 2xl:py-4 rounded-xl 2xl:rounded-2xl font-bold text-xs 2xl:text-lg uppercase tracking-widest transition-colors border border-slate-200 inline-flex items-center shadow-sm">
          ← Return to Dashboard
        </button>
      </div>

      <div class="flex flex-col md:flex-row justify-between items-center mb-6 2xl:mb-12 gap-4 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 px-6 py-4 2xl:px-10 2xl:py-8 rounded-2xl 2xl:rounded-3xl shadow-lg shadow-indigo-500/20 relative overflow-hidden text-white">
        <div class="absolute inset-0 bg-white/5 backdrop-blur-sm mix-blend-overlay"></div>
        <div class="flex items-center gap-4 2xl:gap-8 relative z-10">
          <div class="w-12 h-12 2xl:w-20 2xl:h-20 bg-white/20 backdrop-blur-md rounded-xl 2xl:rounded-2xl flex items-center justify-center shadow-inner border border-white/30 transform hover:scale-105 transition-transform">
            <span class="text-2xl 2xl:text-4xl">{{ selectedCert ? selectedCert.icon : '🧠' }}</span>
          </div>
          <div>
            <h2 class="text-xl 2xl:text-4xl font-black tracking-tight mb-0">
              {{ selectedCert ? selectedCert.code + ' Knowledge Forge' : 'The Knowledge Forge' }}
            </h2>
            <p class="text-indigo-100 font-medium text-[10px] 2xl:text-sm uppercase tracking-widest flex items-center gap-1.5 mt-0.5 2xl:mt-2">
              <span class="w-1.5 h-1.5 2xl:w-2.5 2xl:h-2.5 rounded-full bg-green-400 animate-pulse shadow-[0_0_8px_rgba(74,222,128,1)]"></span>
              Premium Databanks Unlocked
            </p>
          </div>
        </div>
      </div>

      <div class="flex p-2 2xl:p-3 bg-slate-200/50 rounded-3xl 2xl:rounded-[2.5rem] mb-10 2xl:mb-16 w-full max-w-2xl 2xl:max-w-4xl mx-auto shadow-inner border border-slate-200">
        <button @click="changeForgeTab('domains')" :class="activeForgeTab === 'domains' ? 'bg-white text-indigo-600 shadow-sm border border-slate-200' : 'text-slate-500 hover:text-slate-700'" class="flex-1 py-3 md:py-4 2xl:py-6 rounded-2xl 2xl:rounded-3xl text-[10px] md:text-sm 2xl:text-xl font-black uppercase tracking-widest transition-all">
          Study Nodes
        </button>

        <button @click="changeForgeTab('cheatsheets')" :class="activeForgeTab === 'cheatsheets' ? 'bg-white text-emerald-600 shadow-sm border border-slate-200' : 'text-slate-500 hover:text-slate-700'" class="flex-1 py-3 md:py-4 2xl:py-6 rounded-2xl 2xl:rounded-3xl text-[10px] md:text-sm 2xl:text-xl font-black uppercase tracking-widest transition-all">
          Study Material
        </button>

        <button @click="changeForgeTab('study_cards')" :class="activeForgeTab === 'study_cards' ? 'bg-white text-pink-600 shadow-sm border border-slate-200' : 'text-slate-500 hover:text-slate-700'" class="flex-1 py-3 md:py-4 2xl:py-6 rounded-2xl 2xl:rounded-3xl text-[10px] md:text-sm 2xl:text-xl font-black uppercase tracking-widest transition-all">
          Flash Cards
        </button>

        <button @click="changeForgeTab('architecture')" :class="activeForgeTab === 'architecture' ? 'bg-white text-violet-600 shadow-sm border border-slate-200' : 'text-slate-500 hover:text-slate-700'" class="flex-1 py-3 md:py-4 2xl:py-6 rounded-2xl 2xl:rounded-3xl text-[10px] md:text-sm 2xl:text-xl font-black uppercase tracking-widest transition-all">
          Architecture
        </button>

      </div>

      <div v-if="activeForgeTab === 'domains'" class="space-y-6 2xl:space-y-10 max-w-5xl 2xl:max-w-7xl mx-auto animate-fade-in">
        <div v-for="(domain, index) in currentForgeDomains" :key="index"
             class="bg-white rounded-3xl 2xl:rounded-[2.5rem] border border-slate-200 overflow-hidden transition-all"
             :class="(index !== 0 && !isPremiumUser) ? 'bg-slate-50 opacity-80 cursor-not-allowed' : 'shadow-sm hover:shadow-lg hover:border-indigo-300'">

          <button @click.prevent="toggleDomain(index)" class="w-full px-6 py-6 md:px-10 md:py-8 2xl:px-16 2xl:py-12 flex items-center justify-between focus:outline-none group" :class="(index !== 0 && !isPremiumUser) ? 'cursor-not-allowed' : 'bg-white'">
            <div class="flex items-center gap-5 md:gap-8 2xl:gap-12">

              <div class="w-12 h-12 md:w-16 md:h-16 2xl:w-24 2xl:h-24 rounded-full flex items-center justify-center font-black text-lg md:text-xl 2xl:text-4xl transition-transform"
                   :class="(index !== 0 && !isPremiumUser) ? 'bg-slate-200 text-slate-400' : 'bg-indigo-50 text-indigo-500 group-hover:scale-110'">
                <span v-if="index !== 0 && !isPremiumUser">🔒</span>
                <span v-else>{{ index + 1 }}</span>
              </div>

              <div class="text-left">
                <div class="flex items-center gap-3 md:gap-4 2xl:gap-6 mb-1 2xl:mb-3">
                  <h3 class="text-lg md:text-2xl 2xl:text-4xl font-black tracking-tight" :class="(index !== 0 && !isPremiumUser) ? 'text-slate-500' : 'text-slate-900'">{{ domain.title }}</h3>
                  <span v-if="index === 0" class="text-[9px] md:text-xs 2xl:text-sm font-black text-emerald-500 bg-emerald-50 px-2 py-1 md:px-3 2xl:px-4 2xl:py-2 rounded-md border border-emerald-200 uppercase tracking-widest">🔓 Free</span>
                  <span v-else-if="!isPremiumUser" class="text-[9px] md:text-xs 2xl:text-sm font-black text-amber-500 bg-amber-50 px-2 py-1 md:px-3 2xl:px-4 2xl:py-2 rounded-md border border-amber-200 uppercase tracking-widest">⭐ Premium</span>
                </div>
                <span class="text-xs md:text-sm 2xl:text-xl font-black text-slate-400 uppercase tracking-widest">Exam Weight: <span :class="(index !== 0 && !isPremiumUser) ? 'text-slate-400' : 'text-indigo-500'">{{ domain.weight }}</span></span>
              </div>
            </div>
            <span v-if="index === 0 || isPremiumUser" class="text-3xl 2xl:text-5xl text-slate-300 transition-transform duration-300" :class="expandedDomain === index ? 'rotate-180' : ''">↓</span>
          </button>

          <div v-show="expandedDomain === index" class="px-6 pb-6 pt-2 md:px-10 md:pb-10 md:pt-4 2xl:px-16 2xl:pb-16 2xl:pt-6 border-t border-slate-100 bg-slate-50">
            <div class="mb-6 md:mb-8 2xl:mb-12">
              <h4 class="text-xs md:text-sm 2xl:text-xl font-black text-slate-500 uppercase tracking-widest mb-4 2xl:mb-6">Core Topics:</h4>
              <ul class="space-y-3 2xl:space-y-5">
                <li v-for="(topic, tIndex) in domain.topics" :key="tIndex" class="flex items-start gap-3 2xl:gap-5 text-base md:text-lg 2xl:text-2xl text-slate-600 font-medium">
                  <span class="text-indigo-400 mt-1 2xl:mt-2">•</span> {{ topic }}
                </li>
              </ul>
            </div>
            <div class="bg-indigo-100/50 border border-indigo-200 p-5 md:p-8 2xl:p-12 rounded-2xl 2xl:rounded-3xl flex gap-4 md:gap-6 2xl:gap-10 items-start">
              <span class="text-3xl md:text-4xl 2xl:text-6xl">💡</span>
              <div>
                <span class="text-[10px] md:text-xs 2xl:text-base font-black text-indigo-600 uppercase tracking-widest block mb-2 2xl:mb-4">Matrix Fact</span>
                <p class="text-base md:text-lg 2xl:text-2xl font-semibold text-slate-700 leading-relaxed">{{ domain.keyFact }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeForgeTab === 'study_cards'" class="max-w-5xl 2xl:max-w-7xl mx-auto animate-fade-in">

        <div class="flex flex-col lg:flex-row justify-between lg:items-end gap-4 mb-8 2xl:mb-12">
          <div>
            <h3 class="text-2xl 2xl:text-4xl font-black text-slate-800">Flashcard Deck</h3>
            <p class="text-slate-500 text-sm 2xl:text-xl mt-1">Review definitions before entering the Matrix.</p>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 sm:gap-4 items-start sm:items-center">

            <div class="flex bg-slate-200 p-1 rounded-xl 2xl:rounded-2xl border border-slate-300/50">
              <button @click="startWithTerm = false" :class="!startWithTerm ? 'bg-white shadow-sm text-pink-600' : 'text-slate-500 hover:text-slate-700'" class="px-3 py-2 2xl:px-5 2xl:py-3 rounded-lg 2xl:rounded-xl text-xs 2xl:text-base font-bold transition-all">
                Definition First
              </button>
              <button @click="startWithTerm = true" :class="startWithTerm ? 'bg-white shadow-sm text-pink-600' : 'text-slate-500 hover:text-slate-700'" class="px-3 py-2 2xl:px-5 2xl:py-3 rounded-lg 2xl:rounded-xl text-xs 2xl:text-base font-bold transition-all">
                Term First
              </button>
            </div>

            <select
                v-model="librarySelectedDomain"
                class="bg-white border-2 border-slate-200 text-slate-700 text-sm 2xl:text-xl font-bold rounded-xl 2xl:rounded-2xl px-4 py-2.5 2xl:px-6 2xl:py-4 focus:outline-none focus:ring-2 focus:ring-pink-500 cursor-pointer shadow-sm md:min-w-[250px]"
            >
              <option v-for="domain in libraryFlashcardDomains" :key="domain" :value="domain">
                {{ domain === 'All' ? 'All Domains' : domain }}
              </option>
            </select>
          </div>
        </div>

        <div v-if="filteredLibraryFlashcards.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 2xl:gap-10">

          <div
              v-for="card in filteredLibraryFlashcards"
              :key="card.id"
              class="group h-64 2xl:h-80 w-full cursor-pointer [perspective:1000px]"
              @click="card.flipped = !card.flipped"
          >
            <div
                class="relative w-full h-full transition-all duration-500 [transform-style:preserve-3d] shadow-sm hover:shadow-xl rounded-3xl 2xl:rounded-[2.5rem]"
                :class="card.flipped ? '[transform:rotateY(180deg)] shadow-pink-500/20' : ''"
            >
              <div class="absolute inset-0 [backface-visibility:hidden] bg-white border-2 border-slate-100 rounded-3xl 2xl:rounded-[2.5rem] p-6 2xl:p-10 flex flex-col justify-center items-center text-center">
              <span class="text-[10px] 2xl:text-sm font-black uppercase tracking-widest text-slate-400 absolute top-6 2xl:top-8">
                {{ startWithTerm ? 'Term' : 'Definition' }}
              </span>

                <h3 v-if="startWithTerm" class="text-2xl 2xl:text-4xl font-black text-slate-800">{{ card.term }}</h3>
                <p v-else class="text-sm 2xl:text-xl font-medium text-slate-700 leading-relaxed">{{ card.definition }}</p>

                <span class="text-[10px] 2xl:text-sm font-bold text-pink-400 absolute bottom-6 2xl:bottom-8 opacity-0 group-hover:opacity-100 transition-opacity">Click to flip ⤵</span>
              </div>

              <div class="absolute inset-0 [backface-visibility:hidden] [transform:rotateY(180deg)] bg-pink-600 border-2 border-pink-700 rounded-3xl 2xl:rounded-[2.5rem] p-6 2xl:p-10 flex flex-col justify-center items-center text-center shadow-inner">
              <span class="text-[10px] 2xl:text-sm font-black uppercase tracking-widest text-pink-300 absolute top-6 2xl:top-8">
                {{ startWithTerm ? 'Definition' : 'Term' }}
              </span>

                <p v-if="startWithTerm" class="text-sm 2xl:text-xl font-medium text-white leading-relaxed">{{ card.definition }}</p>
                <h3 v-else class="text-2xl 2xl:text-5xl font-black text-white">{{ card.term }}</h3>

                <span class="text-[10px] 2xl:text-sm font-bold text-pink-300 absolute bottom-6 2xl:bottom-8 opacity-0 group-hover:opacity-100 transition-opacity">Click to flip ⤵</span>
              </div>
            </div>
          </div>

        </div>

        <div v-else class="text-center py-20 bg-white rounded-3xl border border-slate-200 border-dashed">
          <div class="text-4xl 2xl:text-6xl mb-4 opacity-50">📭</div>
          <p class="text-slate-500 font-bold 2xl:text-2xl">No flashcards found for this domain.</p>
        </div>
      </div>

      <div v-else-if="activeForgeTab === 'videos'" class="grid grid-cols-1 md:grid-cols-2 gap-6 2xl:gap-12 max-w-5xl 2xl:max-w-7xl mx-auto animate-fade-in">
        <div v-for="(i, index) in 4" :key="i" class="bg-white rounded-3xl 2xl:rounded-[2.5rem] border border-slate-200 p-2 2xl:p-4 transition-all relative overflow-hidden"
             :class="(index !== 0 && !isPremiumUser) ? 'opacity-80 cursor-not-allowed bg-slate-50' : 'shadow-sm group hover:shadow-xl hover:-translate-y-1'">

          <div v-if="index !== 0 && !isPremiumUser" class="absolute inset-0 z-30 bg-slate-900/10 backdrop-blur-[2px] flex items-center justify-center rounded-3xl">
            <div class="bg-white/95 px-6 py-4 md:px-8 md:py-5 2xl:px-12 2xl:py-8 rounded-2xl shadow-xl border border-slate-200 flex flex-col items-center gap-2 2xl:gap-4 transform transition-transform hover:scale-105">
              <span class="text-3xl md:text-4xl 2xl:text-6xl">🔒</span>
              <span class="text-xs md:text-sm 2xl:text-lg font-black text-slate-800 uppercase tracking-widest">Premium Module</span>
            </div>
          </div>

          <div class="bg-slate-900 h-48 2xl:h-80 rounded-2xl 2xl:rounded-3xl relative flex items-center justify-center overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent z-10"></div>
            <div class="absolute top-3 left-3 2xl:top-6 2xl:left-6 z-20">
              <span v-if="index === 0" class="bg-emerald-500 text-white text-[9px] md:text-xs 2xl:text-base font-black uppercase tracking-widest px-2 py-1 md:px-3 2xl:px-4 2xl:py-2 rounded shadow-md">🔓 Free</span>
              <span v-else-if="!isPremiumUser" class="bg-gradient-to-r from-amber-400 to-orange-500 text-white text-[9px] md:text-xs 2xl:text-base font-black uppercase tracking-widest px-2 py-1 md:px-3 2xl:px-4 2xl:py-2 rounded shadow-md">⭐ Premium</span>
            </div>
            <div class="w-16 h-16 2xl:w-24 2xl:h-24 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center z-20 transition-transform border border-white/30" :class="(index === 0 || isPremiumUser) ? 'cursor-pointer group-hover:scale-110' : ''">
              <span class="text-2xl 2xl:text-4xl ml-1 2xl:ml-2">▶️</span>
            </div>
            <span class="absolute bottom-4 right-4 2xl:bottom-6 2xl:right-6 text-white font-mono text-xs md:text-sm 2xl:text-xl z-20 bg-black/50 px-2 py-1 2xl:px-4 2xl:py-2 rounded-md">14:2{{ i }}</span>
          </div>
          <div class="p-5 md:p-6 2xl:p-8">
            <span class="text-[10px] md:text-xs 2xl:text-base font-black text-blue-500 uppercase tracking-widest mb-1 md:mb-2 2xl:mb-3 block">Module 0{{ i }}</span>
            <h3 class="text-lg md:text-xl 2xl:text-3xl font-black mb-1 md:mb-2 2xl:mb-3" :class="(index !== 0 && !isPremiumUser) ? 'text-slate-500' : 'text-slate-900'">Deep Dive: Core Services</h3>
            <p class="text-sm md:text-base 2xl:text-xl text-slate-500 font-medium">Learn how to provision and connect to foundational architecture.</p>
          </div>
        </div>
      </div>

      <div v-else-if="activeForgeTab === 'cheatsheets'" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 2xl:gap-12 max-w-5xl 2xl:max-w-7xl mx-auto animate-fade-in">
        <div v-for="(domain, index) in currentForgeDomains" :key="index"
             @click="(index === 0 || isPremiumUser) ? openStudyMaterial(selectedCert.code, domain.title) : null"
             class="bg-white p-6 md:p-8 2xl:p-12 rounded-[2rem] 2xl:rounded-[3rem] border border-slate-200 transition-all text-center relative overflow-hidden"
             :class="(index !== 0 && !isPremiumUser) ? 'opacity-80 cursor-not-allowed bg-slate-50' : 'shadow-sm hover:shadow-xl hover:-translate-y-1 hover:border-emerald-400 group cursor-pointer'">

          <div v-if="index !== 0 && !isPremiumUser" class="absolute inset-0 z-30 bg-slate-900/5 backdrop-blur-[2px] flex flex-col items-center justify-center rounded-[2rem] 2xl:rounded-[3rem]">
            <div class="bg-white/95 px-6 py-4 md:px-8 md:py-5 2xl:px-12 2xl:py-8 rounded-2xl shadow-xl border border-slate-200 flex flex-col items-center gap-2 2xl:gap-4 transform transition-transform hover:scale-105">
              <span class="text-3xl md:text-4xl 2xl:text-6xl">🔒</span>
              <span class="text-xs md:text-sm 2xl:text-lg font-black text-slate-800 uppercase tracking-widest">Premium Archive</span>
            </div>
          </div>

          <div class="absolute top-4 left-4 2xl:top-8 2xl:left-8 z-20">
            <span v-if="index === 0" class="text-[9px] md:text-xs 2xl:text-sm font-black text-emerald-500 bg-emerald-50 px-2 py-1 md:px-3 2xl:px-4 2xl:py-2 rounded-md border border-emerald-200 uppercase tracking-widest">🔓 Free</span>
          </div>

          <div class="absolute -top-10 -right-10 w-32 h-32 md:w-40 md:h-40 2xl:w-64 2xl:h-64 bg-emerald-50 rounded-full blur-2xl transition-colors" :class="(index === 0 || isPremiumUser) ? 'group-hover:bg-emerald-100' : ''"></div>
          <div class="w-16 h-16 md:w-20 md:h-20 2xl:w-32 2xl:h-32 mx-auto bg-emerald-50 text-emerald-500 rounded-2xl 2xl:rounded-[2rem] flex items-center justify-center text-3xl md:text-4xl 2xl:text-6xl mb-4 md:mb-6 2xl:mb-8 relative z-10 border border-emerald-100 transition-transform" :class="(index === 0 || isPremiumUser) ? 'group-hover:scale-110' : ''">
            📄
          </div>
          <h3 class="text-md md:text-lg 2xl:text-2xl font-black relative z-10" :class="(index !== 0 && !isPremiumUser) ? 'text-slate-500' : 'text-slate-900'">{{ domain.title }}</h3>
          <p class="text-xs md:text-sm 2xl:text-lg text-slate-400 mt-2 md:mt-3 2xl:mt-4 font-bold uppercase tracking-widest relative z-10" :class="(index === 0 || isPremiumUser) ? 'group-hover:text-emerald-500' : ''">Read Study Guide →</p>
        </div>
      </div>

      <div v-if="activeForgeTab === 'architecture'" class="w-full"> <ArchitectureMatrix /></div>

    </div>

    <div v-else-if="currentView === 'lessonViewer' && activeDomain" class="max-w-7xl xl:max-w-[90rem] 2xl:max-w-[110rem] mx-auto px-4 md:px-8 py-6 animate-fade-in-up">

      <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8 2xl:mb-12 border-b border-slate-200 pb-6 2xl:pb-10">
        <div>
          <button @click="currentView = 'library'" class="mb-3 2xl:mb-5 text-[10px] 2xl:text-sm font-black text-slate-400 hover:text-indigo-600 transition-colors uppercase tracking-widest flex items-center gap-1.5 2xl:gap-3 group">
            <span class="group-hover:-translate-x-1 transition-transform">←</span> Return to Databanks
          </button>
          <h2 class="text-3xl 2xl:text-6xl font-black text-slate-900 tracking-tight flex items-center gap-3 2xl:gap-6">
            <span class="text-indigo-500">{{ activeDomain.icon }}</span>
            {{ activeDomain.name }}
          </h2>
        </div>
      </div>

      <div class="flex flex-col lg:flex-row gap-8 2xl:gap-16">

        <div class="lg:w-2/3 flex flex-col gap-6 2xl:gap-10">

          <div class="aspect-video bg-slate-900 rounded-3xl 2xl:rounded-[3rem] shadow-2xl relative overflow-hidden group border border-slate-800">

            <div v-if="!isPlaying" class="absolute inset-0 flex flex-col items-center justify-center z-20">
              <button @click="isPlaying = true" class="w-20 h-20 2xl:w-32 2xl:h-32 bg-indigo-600/90 hover:bg-indigo-500 backdrop-blur-md rounded-full flex items-center justify-center transition-all hover:scale-110 shadow-[0_0_30px_rgba(79,70,229,0.5)] cursor-pointer">
                <span class="text-white text-3xl 2xl:text-6xl ml-2 2xl:ml-4">▶</span>
              </button>
              <p class="text-slate-300 font-bold tracking-widest uppercase text-[10px] 2xl:text-sm mt-6 2xl:mt-10 bg-slate-900/50 px-4 py-1.5 2xl:px-6 2xl:py-2.5 rounded-full backdrop-blur-sm border border-slate-700">
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

                <div class="bg-white px-6 py-4 2xl:px-10 2xl:py-6 border-b border-slate-200 flex justify-between items-center z-10 shadow-sm shrink-0">
                  <h3 class="font-black text-slate-800 text-lg 2xl:text-3xl flex items-center gap-2 2xl:gap-4">
                    📄 {{ activeModule?.title || 'Classified Document' }}
                  </h3>
                  <div class="flex gap-3 2xl:gap-5">
                    <button @click="downloadDocumentPDF" class="flex items-center gap-2 2xl:gap-3 bg-slate-800 hover:bg-slate-900 text-white px-4 py-2 2xl:px-6 2xl:py-3 rounded-lg 2xl:rounded-xl font-bold text-xs 2xl:text-lg uppercase tracking-widest transition-all hover:-translate-y-0.5 shadow-md">
                      ⬇️ Save PDF
                    </button>
                    <button @click="completeActiveModule" class="flex items-center gap-2 2xl:gap-3 bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 2xl:px-6 2xl:py-3 rounded-lg 2xl:rounded-xl font-bold text-xs 2xl:text-lg uppercase tracking-widest transition-all hover:-translate-y-0.5 shadow-md shadow-indigo-500/30">
                      ✓ Claim XP
                    </button>
                  </div>
                </div>

                <div class="p-4 md:p-8 2xl:p-12 overflow-y-auto flex-grow flex justify-center bg-slate-100/50">
                  <div class="bg-white w-full max-w-3xl 2xl:max-w-5xl p-8 md:p-12 2xl:p-16 rounded-xl 2xl:rounded-2xl shadow-md border border-slate-200 min-h-full">
                    <div class="prose prose-slate 2xl:prose-xl max-w-none font-serif text-slate-700 whitespace-pre-wrap leading-relaxed">
                      {{ activeModule?.content || 'Awaiting transmission... No document content found.' }}
                    </div>
                  </div>
                </div>

              </div>

            </div>

            <div v-if="!isPlaying" class="absolute inset-0 bg-gradient-to-br from-indigo-900/40 via-slate-900 to-purple-900/40"></div>
          </div>

          <div v-if="activeModule" class="bg-white p-6 2xl:p-10 rounded-3xl 2xl:rounded-[2.5rem] border border-slate-200 shadow-sm">
      <span class="text-[10px] 2xl:text-sm font-black text-indigo-600 tracking-widest uppercase bg-indigo-50 px-3 py-1 2xl:px-5 2xl:py-2 rounded-full mb-3 2xl:mb-5 inline-block">
        Current Objective
      </span>
            <h3 class="text-2xl 2xl:text-4xl font-black text-slate-900 mb-2 2xl:mb-4">{{ activeModule.title }}</h3>
            <p class="text-sm 2xl:text-xl text-slate-500 font-medium leading-relaxed">
              Focus on understanding the core principles presented in this module. Take notes on key terminology as it will heavily influence your Sudden Death survival rate.
            </p>
          </div>
        </div>

        <div class="lg:w-1/3">
          <div class="bg-white rounded-3xl 2xl:rounded-[2.5rem] border border-slate-200 shadow-sm sticky top-8 overflow-hidden flex flex-col max-h-[800px] 2xl:max-h-[1200px]">

            <div class="p-6 2xl:p-10 border-b border-slate-100 bg-slate-50/50">
              <h4 class="text-sm 2xl:text-2xl font-black text-slate-900 uppercase tracking-widest">Training Modules</h4>
              <p class="text-[11px] 2xl:text-base text-slate-500 mt-1 2xl:mt-2 font-medium">{{ activeDomain?.modules?.length || 0 }} protocols available</p>
            </div>

            <div class="overflow-y-auto p-3 2xl:p-6 flex flex-col gap-2 2xl:gap-4">
              <button
                  v-for="(module, index) in activeDomain?.modules"
                  :key="module.id"
                  @click="activeModule = module"
                  class="w-full flex items-start gap-4 2xl:gap-6 p-4 2xl:p-6 rounded-2xl 2xl:rounded-3xl transition-all text-left group"
                  :class="activeModule?.id === module.id ? 'bg-indigo-50 border border-indigo-100 shadow-sm' : 'hover:bg-slate-50 border border-transparent hover:border-slate-100'"
              >
                <div class="mt-0.5 2xl:mt-1 shrink-0">
                  <span v-if="activeModule?.id === module.id" class="flex h-6 w-6 2xl:w-10 2xl:h-10 items-center justify-center rounded-full bg-indigo-600 text-white shadow-md shadow-indigo-500/30 text-[10px] 2xl:text-base">▶</span>
                  <span v-else-if="module.type === 'document'" class="flex h-6 w-6 2xl:w-10 2xl:h-10 items-center justify-center rounded-full bg-slate-100 text-slate-400 group-hover:text-slate-600 text-[10px] 2xl:text-base">📄</span>
                  <span v-else class="flex h-6 w-6 2xl:w-10 2xl:h-10 items-center justify-center rounded-full bg-slate-100 text-slate-400 group-hover:text-slate-600 text-[10px] 2xl:text-base">📺</span>
                </div>

                <div class="flex-grow">
                  <h5 class="text-sm 2xl:text-xl font-bold leading-tight mb-1 2xl:mb-2 flex items-center gap-2 flex-wrap" :class="activeModule?.id === module.id ? 'text-indigo-900' : 'text-slate-700 group-hover:text-slate-900'">
                    <span>{{ index + 1 }}. {{ module.title }}</span>

                    <span
                        class="text-[9px] 2xl:text-xs uppercase tracking-wider font-black px-1.5 py-0.5 2xl:px-2 2xl:py-1 rounded border"
                        :class="module.type === 'video' ? 'bg-indigo-100 text-indigo-600 border-indigo-200' : 'bg-emerald-100 text-emerald-600 border-emerald-200'"
                    >
        {{ module.type === 'video' ? '▶ Video' : '📄 Doc' }}
      </span>
                  </h5>

                  <span class="text-[10px] 2xl:text-sm font-bold tracking-wider uppercase text-slate-400" :class="activeModule?.id === module.id ? 'text-indigo-400' : ''">
      {{ module.duration }}
    </span>
                </div>
              </button>

              <div v-if="!activeDomain?.modules || activeDomain.modules.length === 0" class="text-center p-8 2xl:p-12 text-slate-400">
                <p class="text-xs 2xl:text-lg font-bold uppercase tracking-widest">No modules loaded</p>
              </div>

            </div>

          </div>
        </div>

      </div>
    </div>

    <div v-else-if="currentView === 'admin'" class="max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 py-8 animate-fade-in admin-dashboard">

      <button @click="currentView = 'landing'" class="mb-6 2xl:mb-10 bg-slate-100 hover:bg-slate-200 text-slate-600 px-5 py-2.5 2xl:px-8 2xl:py-4 rounded-xl 2xl:rounded-2xl font-bold text-xs 2xl:text-base uppercase tracking-widest transition-colors border border-slate-200 inline-flex items-center shadow-sm">
        ← Return to Admin Hub
      </button>

      <div class="mb-8 2xl:mb-12 bg-slate-800 p-6 md:p-8 2xl:p-12 rounded-2xl 2xl:rounded-[2rem] border border-slate-700 relative overflow-hidden shadow-xl text-left">
        <h3 class="text-xl 2xl:text-3xl font-black text-white mb-2 2xl:mb-4 flex items-center gap-3">
          <span class="p-2 2xl:p-3 bg-indigo-500/20 text-indigo-400 rounded-lg 2xl:rounded-xl">🗄️</span>
          Mass Databank Injection
        </h3>
        <p class="text-sm 2xl:text-xl text-slate-400 mb-6 2xl:mb-10 max-w-2xl 2xl:max-w-4xl">Upload official AWS data sets (.csv) to update the Knowledge Forge. Ensure columns match the master schema.</p>

        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 2xl:gap-10">

          <div class="bg-slate-900/50 p-6 2xl:p-8 rounded-xl 2xl:rounded-2xl border border-slate-700 flex flex-col justify-between">
            <div>
              <h4 class="text-sm 2xl:text-lg font-black text-indigo-400 uppercase tracking-widest mb-2">Questions CSV</h4>
              <p class="text-[10px] 2xl:text-sm text-slate-500 mb-4 2xl:mb-6 font-bold uppercase tracking-wider">9 Column Schema</p>
              <input type="file" accept=".csv" @change="handleFileSelect" class="w-full text-xs 2xl:text-base text-slate-400 file:mr-4 file:py-2 2xl:file:py-3 file:px-4 2xl:file:px-6 file:rounded-lg 2xl:file:rounded-xl file:border-0 file:font-black file:uppercase file:bg-indigo-500/20 file:text-indigo-400 bg-slate-950 border border-slate-800 rounded-lg 2xl:rounded-xl cursor-pointer mb-4 2xl:mb-6" />
            </div>
            <div>
              <button @click="uploadCsv" :disabled="!selectedCsvFile" class="py-3 2xl:py-4 rounded-lg 2xl:rounded-xl font-black text-xs 2xl:text-base uppercase tracking-widest transition-all w-full" :class="selectedCsvFile ? 'bg-indigo-600 text-white hover:bg-indigo-500 shadow-lg shadow-indigo-500/30' : 'bg-slate-800 text-slate-500 cursor-not-allowed'">
                Inject Questions
              </button>
              <p v-if="uploadStatus" class="text-[10px] 2xl:text-sm font-bold mt-3 text-center" :class="uploadStatus.includes('✅') ? 'text-emerald-400' : 'text-amber-400'">{{ uploadStatus }}</p>
            </div>
          </div>

          <div class="bg-slate-900/50 p-6 2xl:p-8 rounded-xl 2xl:rounded-2xl border border-slate-700 flex flex-col justify-between">
            <div>
              <h4 class="text-sm 2xl:text-lg font-black text-pink-400 uppercase tracking-widest mb-2">Flashcards CSV</h4>
              <p class="text-[10px] 2xl:text-sm text-slate-500 mb-4 2xl:mb-6 font-bold uppercase tracking-wider">4 Column Schema</p>
              <input type="file" accept=".csv" @change="handleFlashcardSelect" class="w-full text-xs 2xl:text-base text-slate-400 file:mr-4 file:py-2 2xl:file:py-3 file:px-4 2xl:file:px-6 file:rounded-lg 2xl:file:rounded-xl file:border-0 file:font-black file:uppercase file:bg-pink-500/20 file:text-pink-400 bg-slate-950 border border-slate-800 rounded-lg 2xl:rounded-xl cursor-pointer mb-4 2xl:mb-6" />
            </div>
            <div>
              <button @click="uploadFlashcardCsv" :disabled="!selectedFlashcardCsv" class="py-3 2xl:py-4 rounded-lg 2xl:rounded-xl font-black text-xs 2xl:text-base uppercase tracking-widest transition-all w-full" :class="selectedFlashcardCsv ? 'bg-pink-600 text-white hover:bg-pink-500 shadow-lg shadow-pink-500/30' : 'bg-slate-800 text-slate-500 cursor-not-allowed'">
                Inject Flashcards
              </button>
              <p v-if="flashcardUploadStatus" class="text-[10px] 2xl:text-sm font-bold mt-3 text-center" :class="flashcardUploadStatus.includes('✅') ? 'text-emerald-400' : 'text-amber-400'">{{ flashcardUploadStatus }}</p>
            </div>
          </div>

          <div class="bg-slate-900/50 p-6 2xl:p-8 rounded-xl 2xl:rounded-2xl border border-slate-700 flex flex-col justify-between">
            <div>
              <h4 class="text-sm 2xl:text-lg font-black text-cyan-400 uppercase tracking-widest mb-2">Study Material</h4>
              <p class="text-[10px] 2xl:text-sm text-slate-500 mb-4 2xl:mb-6 font-bold uppercase tracking-wider">4 Column Schema</p>
              <input type="file" accept=".csv" @change="handleStudyFileSelect" class="w-full text-xs 2xl:text-base text-slate-400 file:mr-4 file:py-2 2xl:file:py-3 file:px-4 2xl:file:px-6 file:rounded-lg 2xl:file:rounded-xl file:border-0 file:font-black file:uppercase file:bg-cyan-500/20 file:text-cyan-400 bg-slate-950 border border-slate-800 rounded-lg 2xl:rounded-xl cursor-pointer mb-4 2xl:mb-6" />
            </div>
            <div>
              <button @click="submitStudyCsv" :disabled="!studyFile" class="py-3 2xl:py-4 rounded-lg 2xl:rounded-xl font-black text-xs 2xl:text-base uppercase tracking-widest transition-all w-full" :class="studyFile ? 'bg-cyan-600 text-white hover:bg-cyan-500 shadow-lg shadow-cyan-500/30' : 'bg-slate-800 text-slate-500 cursor-not-allowed'">
                Inject Study Docs
              </button>
              <p v-if="studyUploadStatus" class="text-[10px] 2xl:text-sm font-bold mt-3 text-center" :class="studyUploadStatus.includes('✅') ? 'text-cyan-400' : 'text-amber-400'">{{ studyUploadStatus }}</p>
            </div>
          </div>

          <div class="bg-slate-900/50 p-6 2xl:p-8 rounded-xl 2xl:rounded-2xl border border-slate-700 flex flex-col justify-between">
            <div>
              <h4 class="text-sm 2xl:text-lg font-black text-emerald-400 uppercase tracking-widest mb-2">Architecture</h4>
              <p class="text-[10px] 2xl:text-sm text-slate-500 mb-4 2xl:mb-6 font-bold uppercase tracking-wider">Pipeline Schema</p>
              <input type="file" accept=".csv" @change="handlePipelineFileSelect" class="w-full text-xs 2xl:text-base text-slate-400 file:mr-4 file:py-2 2xl:file:py-3 file:px-4 2xl:file:px-6 file:rounded-lg 2xl:file:rounded-xl file:border-0 file:font-black file:uppercase file:bg-emerald-500/20 file:text-emerald-400 bg-slate-950 border border-slate-800 rounded-lg 2xl:rounded-xl cursor-pointer mb-4 2xl:mb-6" />
            </div>
            <div>
              <button @click="submitPipelineCsv" :disabled="!pipelineFile" class="py-3 2xl:py-4 rounded-lg 2xl:rounded-xl font-black text-xs 2xl:text-base uppercase tracking-widest transition-all w-full" :class="pipelineFile ? 'bg-emerald-600 text-white hover:bg-emerald-500 shadow-lg shadow-emerald-500/30' : 'bg-slate-800 text-slate-500 cursor-not-allowed'">
                Inject Pipelines
              </button>
              <p v-if="pipelineUploadStatus" class="text-[10px] 2xl:text-sm font-bold mt-3 text-center" :class="pipelineUploadStatus.includes('✅') ? 'text-emerald-400' : 'text-amber-400'">{{ pipelineUploadStatus }}</p>
            </div>
          </div>

        </div>
      </div>

      <div class="flex gap-4 2xl:gap-8 mb-6 2xl:mb-10 border-b border-slate-700">
        <button @click="adminActiveTab = 'questions'" :class="adminActiveTab === 'questions' ? 'text-indigo-400 border-b-2 border-indigo-400 pb-3 2xl:pb-4' : 'text-slate-400 hover:text-white pb-3 2xl:pb-4'" class="font-black tracking-widest uppercase text-xs 2xl:text-lg transition-all">
          Questions Matrix
        </button>
        <button @click="adminActiveTab = 'flashcards'" :class="adminActiveTab === 'flashcards' ? 'text-pink-400 border-b-2 border-pink-400 pb-3 2xl:pb-4' : 'text-slate-400 hover:text-white pb-3 2xl:pb-4'" class="font-black tracking-widest uppercase text-xs 2xl:text-lg transition-all">
          Flashcard Matrix
        </button>
        <button @click="adminActiveTab = 'architecture'" :class="adminActiveTab === 'architecture' ? 'text-emerald-400 border-b-2 border-emerald-400 pb-3 2xl:pb-4' : 'text-slate-400 hover:text-white pb-3 2xl:pb-4'" class="font-black tracking-widest uppercase text-xs 2xl:text-lg transition-all">
          Architecture Data
        </button>
        <button @click="adminActiveTab = 'analytics'; loadAdminAnalytics()" :class="adminActiveTab === 'analytics' ? 'text-amber-400 border-b-2 border-amber-400 pb-3 2xl:pb-4' : 'text-slate-400 hover:text-white pb-3 2xl:pb-4'" class="font-black tracking-widest uppercase text-xs 2xl:text-lg transition-all">
          📊 Analytics
        </button>
      </div>

      <!-- 📊 ANALYTICS TAB -->
      <div v-if="adminActiveTab === 'analytics'" class="animate-fade-in">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 2xl:gap-10 mb-8">

          <div class="bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border border-slate-200 shadow-sm">
            <h3 class="text-sm 2xl:text-lg font-black text-slate-800 uppercase tracking-widest mb-4 2xl:mb-6 flex items-center gap-2">
              <span class="text-indigo-500">🎮</span> Game Mode Popularity
            </h3>
            <canvas ref="gameModeChartRef" height="220"></canvas>
          </div>

          <div class="bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border border-slate-200 shadow-sm">
            <h3 class="text-sm 2xl:text-lg font-black text-slate-800 uppercase tracking-widest mb-4 2xl:mb-6 flex items-center gap-2">
              <span class="text-emerald-500">🏆</span> Certification Popularity
            </h3>
            <canvas ref="certPopChartRef" height="220"></canvas>
          </div>

        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 2xl:gap-8 mb-8">
          <div class="bg-gradient-to-br from-indigo-500 to-blue-600 p-6 2xl:p-8 rounded-2xl 2xl:rounded-3xl text-center text-white shadow-lg">
            <div class="text-3xl 2xl:text-5xl font-black">{{ adminAnalytics.totalGameSessions || 0 }}</div>
            <div class="text-xs 2xl:text-sm font-bold uppercase tracking-widest mt-1 opacity-80">Total Game Sessions</div>
          </div>
          <div class="bg-gradient-to-br from-emerald-500 to-green-600 p-6 2xl:p-8 rounded-2xl 2xl:rounded-3xl text-center text-white shadow-lg">
            <div class="text-3xl 2xl:text-5xl font-black">{{ adminAnalytics.totalStudyViews || 0 }}</div>
            <div class="text-xs 2xl:text-sm font-bold uppercase tracking-widest mt-1 opacity-80">Study Material Views</div>
          </div>
          <div class="bg-gradient-to-br from-purple-500 to-violet-600 p-6 2xl:p-8 rounded-2xl 2xl:rounded-3xl text-center text-white shadow-lg">
            <div class="text-3xl 2xl:text-5xl font-black">{{ adminAnalytics.topMode || '-' }}</div>
            <div class="text-xs 2xl:text-sm font-bold uppercase tracking-widest mt-1 opacity-80">Most Popular Mode</div>
          </div>
          <div class="bg-gradient-to-br from-amber-500 to-orange-600 p-6 2xl:p-8 rounded-2xl 2xl:rounded-3xl text-center text-white shadow-lg">
            <div class="text-3xl 2xl:text-5xl font-black">{{ adminAnalytics.topCert || '-' }}</div>
            <div class="text-xs 2xl:text-sm font-bold uppercase tracking-widest mt-1 opacity-80">Most Popular Cert</div>
          </div>
        </div>

        <div class="bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border border-slate-200 shadow-sm">
          <h3 class="text-sm 2xl:text-lg font-black text-slate-800 uppercase tracking-widest mb-4 2xl:mb-6 flex items-center gap-2">
            <span class="text-pink-500">📈</span> Games vs. Study Material (Engagement Split)
          </h3>
          <canvas ref="engagementSplitChartRef" height="160"></canvas>
        </div>
      </div>

      <div v-if="adminActiveTab === 'questions'" class="animate-fade-in">
        <div class="admin-header 2xl:mb-8 flex justify-between items-end">
          <h2 class="2xl:text-3xl font-black text-slate-800">QUESTION DATABASE</h2>
          <button @click="openAdminModal(null)" class="btn-create 2xl:text-lg 2xl:px-6 2xl:py-3">+ INJECT NEW QUESTION</button>
        </div>

        <table class="admin-table 2xl:text-lg w-full">
          <thead>
          <tr>
            <th class="2xl:p-4">ID</th>
            <th class="2xl:p-4">Cert</th>
            <th class="2xl:p-4">Category</th>
            <th class="2xl:p-4">Question Text</th>
            <th class="2xl:p-4 text-right">Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="q in allAdminQuestions" :key="q.id">
            <td class="2xl:p-4">{{ q.id }}</td>
            <td class="2xl:p-4"><span class="badge 2xl:text-sm">{{ q.certCode }}</span></td>
            <td class="2xl:p-4">{{ q.domain }}</td>
            <td class="truncate 2xl:p-4 max-w-xs xl:max-w-md">{{ q.questionText }}</td>
            <td class="action-buttons 2xl:p-4 text-right">
              <button @click="openAdminModal(q)" class="btn-edit 2xl:px-4 2xl:py-2 mr-2">EDIT</button>
              <button @click="deleteAdminQuestion(q.id)" class="btn-delete 2xl:px-4 2xl:py-2">VAPORIZE</button>
            </td>
          </tr>
          <tr v-if="allAdminQuestions.length === 0">
            <td colspan="5" class="text-center text-slate-400 py-8 2xl:py-16 font-bold uppercase tracking-widest text-xs 2xl:text-base">
              No questions in databank. Inject CSV to begin.
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="adminActiveTab === 'flashcards'" class="animate-fade-in">
        <div class="admin-header 2xl:mb-8">
          <h2 class="2xl:text-3xl font-black text-slate-800">FLASHCARD DATABASE</h2>
        </div>

        <table class="admin-table 2xl:text-lg w-full">
          <thead>
          <tr>
            <th class="2xl:p-4">ID</th>
            <th class="2xl:p-4">Cert</th>
            <th class="2xl:p-4">Category</th>
            <th class="2xl:p-4">Term</th>
            <th class="2xl:p-4">Definition</th>
            <th class="2xl:p-4 text-right">Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="f in allAdminFlashcards" :key="'f-'+f.id">
            <td class="2xl:p-4">{{ f.id }}</td>
            <td class="2xl:p-4"><span class="badge 2xl:text-sm">{{ f.examCode }}</span></td>
            <td class="2xl:p-4">{{ f.category }}</td>
            <td class="font-bold text-pink-600 2xl:p-4">{{ f.term }}</td>
            <td class="truncate 2xl:p-4 max-w-xs xl:max-w-md" :title="f.definition">{{ f.definition }}</td>
            <td class="action-buttons 2xl:p-4 text-right">
              <button @click="deleteAdminFlashcard(f.id)" class="btn-delete 2xl:px-4 2xl:py-2">VAPORIZE</button>
            </td>
          </tr>
          <tr v-if="!allAdminFlashcards || allAdminFlashcards.length === 0">
            <td colspan="6" class="text-center text-slate-400 py-8 2xl:py-16 font-bold uppercase tracking-widest text-xs 2xl:text-base">
              No flashcards in databank. Inject CSV to begin.
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="adminActiveTab === 'architecture'" class="animate-fade-in">
        <div class="admin-header 2xl:mb-8 flex justify-between items-end">
          <h2 class="2xl:text-3xl font-black text-slate-800">ARCHITECTURE PIPELINES</h2>
        </div>

        <table class="admin-table 2xl:text-lg w-full">
          <thead>
          <tr>
            <th class="2xl:p-4">ID</th>
            <th class="2xl:p-4">Cert</th>
            <th class="2xl:p-4">Scenario / Title</th>
            <th class="2xl:p-4 text-right">Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="p in allAdminPipelines" :key="'p-'+p.id">
            <td class="2xl:p-4">{{ p.id }}</td>

            <td class="2xl:p-4"><span class="badge 2xl:text-sm">{{ p.examCode }}</span></td>

            <td class="font-bold text-emerald-600 2xl:p-4 truncate max-w-xs xl:max-w-md" :title="p.scenarioDescription">
              {{ p.scenarioDescription }}
            </td>

            <td class="action-buttons 2xl:p-4 text-right">
              <button @click="deleteAdminPipeline(p.id)" class="btn-delete 2xl:px-4 2xl:py-2">VAPORIZE</button>
            </td>
          </tr>
          <tr v-if="!allAdminPipelines || allAdminPipelines.length === 0">
            <td colspan="4" class="text-center text-slate-400 py-8 2xl:py-16 font-bold uppercase tracking-widest text-xs 2xl:text-base">
              No architecture pipelines in databank. Inject CSV to begin.
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="showAdminModal" class="admin-modal-overlay">
        <div class="admin-modal-content max-h-[90vh] overflow-y-auto 2xl:max-w-4xl 2xl:p-12">
          <h3 class="2xl:text-3xl 2xl:mb-8">{{ isAdminEditing ? 'EDIT DATA NODE' : 'INJECT NEW DATA NODE' }}</h3>

          <label class="2xl:text-xl">Certification Code</label>
          <input v-model="adminForm.examCode" placeholder="e.g. SCS-C02" class="2xl:text-xl 2xl:p-4 2xl:mb-6" />

          <label class="2xl:text-xl">Category</label>
          <input v-model="adminForm.category" placeholder="e.g. Security" class="2xl:text-xl 2xl:p-4 2xl:mb-6" />

          <label class="2xl:text-xl">Question Text</label>
          <textarea v-model="adminForm.text" rows="3" class="2xl:text-xl 2xl:p-4 2xl:mb-6"></textarea>

          <div class="flex justify-between items-end mb-1 mt-2 2xl:mb-4 2xl:mt-6">
            <label class="!mb-0 2xl:text-xl">Response Options (Check box if correct)</label>
            <button
                v-if="adminForm.options && adminForm.options.length < 6"
                @click.prevent="addOption"
                class="text-[10px] 2xl:text-sm bg-indigo-100 text-indigo-700 px-2 py-1 2xl:px-4 2xl:py-2 rounded font-black uppercase tracking-widest hover:bg-indigo-200 transition-colors">
              + Add Option
            </button>
          </div>

          <div class="space-y-2 2xl:space-y-4 mb-4 2xl:mb-8 bg-slate-50 p-4 2xl:p-8 rounded-xl border border-slate-200 shadow-inner">
            <div v-for="(opt, index) in adminForm.options" :key="index" class="flex items-center gap-3 2xl:gap-6">

              <input
                  type="checkbox"
                  v-model="opt.isCorrect"
                  class="w-5 h-5 2xl:w-8 2xl:h-8 text-emerald-500 rounded border-slate-300 focus:ring-emerald-500 cursor-pointer shadow-sm"
                  :title="opt.isCorrect ? 'Marked as Correct' : 'Marked as Incorrect'"
              />

              <input
                  v-model="opt.text"
                  :placeholder="'Option ' + (index + 1) + ' text...'"
                  class="flex-grow !mb-0 !mt-0 2xl:text-xl 2xl:p-3"
                  :class="opt.isCorrect ? 'border-emerald-500 bg-emerald-50/50' : 'border-slate-300 bg-white'"
              />

              <button
                  v-if="adminForm.options && adminForm.options.length > 2"
                  @click.prevent="removeOption(index)"
                  class="w-8 h-8 2xl:w-12 2xl:h-12 flex items-center justify-center bg-red-100 text-red-600 rounded-lg 2xl:rounded-xl font-black 2xl:text-2xl hover:bg-red-200 transition-colors flex-shrink-0"
                  title="Remove Option">
                ×
              </button>
            </div>
          </div>
          <label class="2xl:text-xl">Explanation</label>
          <textarea v-model="adminForm.explanation" rows="2" class="2xl:text-xl 2xl:p-4 2xl:mb-8"></textarea>

          <div class="modal-actions mt-4 2xl:mt-8 flex gap-4">
            <button @click="showAdminModal = false" class="btn-cancel 2xl:text-xl 2xl:px-8 2xl:py-4">CANCEL</button>
            <button @click="saveAdminQuestion" class="btn-save 2xl:text-xl 2xl:px-8 2xl:py-4">OVERWRITE MAINFRAME</button>
          </div>
        </div>
      </div>

    </div>

    <div v-if="showAbortModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm animate-fade-in px-4">
      <div class="bg-slate-900 border border-red-500/30 rounded-2xl p-6 md:p-8 max-w-md w-full shadow-2xl shadow-red-900/20 text-center relative overflow-hidden">

        <div class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-transparent via-red-500 to-transparent opacity-50"></div>

        <div class="w-16 h-16 bg-red-500/10 rounded-full flex items-center justify-center mx-auto mb-6 border border-red-500/20">
          <span class="text-3xl">⚠️</span>
        </div>

        <h3 class="text-xl md:text-2xl font-black text-white mb-2 uppercase tracking-widest">Abort Simulation?</h3>
        <p class="text-slate-400 mb-8 font-medium text-sm md:text-base leading-relaxed">
          Are you sure you want to exit? Your progress will be wiped, and no score or XP will be recorded.
        </p>

        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button @click="showAbortModal = false" class="px-6 py-3 rounded-xl font-bold text-slate-300 bg-slate-800 hover:bg-slate-700 transition-colors w-full sm:w-auto border border-slate-700">
            CANCEL
          </button>
          <button @click="confirmAbort" class="px-6 py-3 rounded-xl font-black tracking-wider text-white bg-red-600 hover:bg-red-500 transition-colors w-full sm:w-auto shadow-lg shadow-red-600/30 active:scale-95">
            YES, ABORT
          </button>
        </div>
      </div>
    </div>

    <StudyMaterialView
        v-if="currentView === 'study_reader'"
        :certCode="studyActiveCert"
        :domain="studyActiveDomain"
        @back="currentView = 'library'"
    />

    <div v-if="showGuestLimitModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-md z-[400] flex items-center justify-center p-4">
      <div class="bg-slate-900 border border-cyan-500/50 rounded-[2rem] w-full max-w-md 2xl:max-w-2xl overflow-hidden shadow-[0_0_30px_rgba(6,182,212,0.3)] relative p-10 2xl:p-16 text-center">
        <div class="text-5xl 2xl:text-7xl mb-4 2xl:mb-8 animate-pulse">🛑</div>
        <h2 class="text-2xl 2xl:text-4xl font-black text-cyan-400 mb-4 2xl:mb-6 font-mono uppercase tracking-widest">Free Compute Exhausted</h2>
        <p class="text-slate-300 mb-8 2xl:mb-12 font-mono text-sm 2xl:text-xl leading-relaxed italic">"You've taken your first step into a larger world..."<br><br><span class="not-italic text-slate-400 text-xs 2xl:text-base">However, unauthorized access to the deeper Jedi Archives is restricted. Register your biometric signature to unlock the full simulator and save your history.</span></p>
        <div class="flex flex-col gap-3 2xl:gap-5">
          <button @click="showGuestLimitModal = false; showAuthModal = true; isSignup = true" class="w-full bg-cyan-600 text-white py-4 2xl:py-6 rounded-xl 2xl:rounded-2xl font-black shadow-[0_0_15px_rgba(6,182,212,0.5)] hover:bg-cyan-500 hover:scale-[1.02] transition-all uppercase tracking-widest text-sm 2xl:text-xl">Create Account</button>
          <button @click="showGuestLimitModal = false; goBackToLanding()" class="w-full bg-slate-800 text-slate-400 py-4 2xl:py-6 rounded-xl 2xl:rounded-2xl font-bold border border-slate-700 hover:bg-slate-700 hover:text-white transition-all uppercase tracking-widest text-xs 2xl:text-lg">Return to Base</button>
        </div>
      </div>
    </div>

    <div v-if="showReviewModal" class="max-w-7xl 2xl:max-w-[100rem] mx-auto px-4 md:px-8 relative z-50">
      <div class="bg-white rounded-[2rem] 2xl:rounded-[3rem] shadow-2xl overflow-hidden border border-slate-100">
        <div class="bg-slate-50 p-10 2xl:p-16 border-b border-slate-200">
          <h1 class="text-4xl md:text-5xl 2xl:text-7xl font-black text-slate-950 tracking-tight">Exam Review</h1>
          <p class="text-slate-600 mt-1 2xl:mt-3 uppercase text-xs 2xl:text-xl font-bold tracking-widest">Analyzing Missed Concepts</p>
        </div>
        <div class="p-8 md:p-12 2xl:p-20 max-h-[75vh] 2xl:max-h-[80vh] overflow-y-auto space-y-12 2xl:space-y-20 custom-scrollbar">
          <div v-for="(q, index) in reviewQuestions" :key="q.id" class="relative pl-12 2xl:pl-20">
            <div class="flex justify-between items-center mb-6 2xl:mb-10">
              <div class="flex items-center gap-3 2xl:gap-6">
                <div class="absolute left-0 top-0 w-8 h-8 2xl:w-12 2xl:h-12 rounded-full bg-gradient-to-br from-blue-600 to-blue-500 text-white flex items-center justify-center font-black text-sm 2xl:text-xl shadow-md">{{ index + 1 }}</div>
                <h3 class="text-xl md:text-2xl 2xl:text-4xl font-extrabold text-slate-900 leading-tight">Question Details</h3>
              </div>
              <span :class="userResults[q.id] ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'" class="font-black uppercase text-xs 2xl:text-lg tracking-wider px-3 py-1 2xl:px-5 2xl:py-2 rounded-full flex items-center gap-1.5 2xl:gap-3">{{ userResults[q.id] ? '✓ Correct' : '✗ Incorrect' }}</span>
            </div>
            <p class="text-slate-800 text-lg 2xl:text-3xl leading-relaxed mb-8 2xl:mb-12">{{ q.questionText }}</p>
            <div class="bg-blue-50/50 p-6 md:p-8 2xl:p-12 rounded-3xl 2xl:rounded-[2.5rem] border border-blue-100 shadow-inner relative">
              <svg class="absolute -top-4 -left-4 2xl:-top-6 2xl:-left-6 w-10 h-10 2xl:w-16 2xl:h-16 text-blue-200 opacity-60" fill="currentColor" viewBox="0 0 24 24"><path d="M13 14.725c0-5.141 3.892-10.519 10-11.725l.943 1.956c-3.18 1.018-4.943 3.903-4.943 6.451 0 2.067 1.341 3.067 2.499 3.067 1.366 0 2.501 1.258 2.501 2.91 0 2.228-2.028 4.616-5.123 4.616-3.745 0-5.877-2.926-5.877-7.275zm-12 0c0-5.141 3.892-10.519 10-11.725l.943 1.956c-3.18 1.018-4.943 3.903-4.943 6.451 0 2.067 1.341 3.067 2.499 3.067 1.366 0 2.501 1.258 2.501 2.91 0 2.228-2.028 4.616-5.123 4.616-3.745 0-5.877-2.926-5.877-7.275z"/></svg>
              <p class="text-blue-600 text-[11px] 2xl:text-base font-black uppercase tracking-widest mb-1.5 2xl:mb-3">Detailed Explanation</p>
              <p class="text-sm md:text-base 2xl:text-2xl italic leading-relaxed text-blue-900/90">{{ q.explanation }}</p>
            </div>
            <div class="h-px bg-slate-100 mt-12 2xl:mt-20 w-full"></div>
          </div>
        </div>
        <div class="bg-slate-50 p-8 2xl:p-12 border-t border-slate-200 mt-4 2xl:mt-8 rounded-b-[2rem] 2xl:rounded-b-[3rem]">
          <div class="flex flex-col md:flex-row justify-center items-center gap-4 2xl:gap-8">
            <button @click="handlePrint" class="w-full md:w-auto flex items-center justify-center gap-2 2xl:gap-4 bg-slate-800 hover:bg-slate-950 text-white py-4 px-10 2xl:py-6 2xl:px-16 rounded-2xl 2xl:rounded-[2rem] font-black 2xl:text-2xl shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">Export to PDF</button>
            <button @click="closeReview" class="w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white py-4 px-12 2xl:py-6 2xl:px-20 rounded-2xl 2xl:rounded-[2rem] font-black 2xl:text-2xl shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">✓ Got it! Close Review</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showSuccessHologram" class="fixed inset-0 z-[500] flex items-center justify-center bg-slate-900/90 backdrop-blur-md">
      <div class="relative bg-black border-2 border-cyan-400 shadow-[0_0_50px_rgba(34,211,238,0.4)] rounded-[2.5rem] 2xl:rounded-[4rem] p-12 2xl:p-20 max-w-lg 2xl:max-w-2xl text-center overflow-hidden">

        <div class="absolute inset-0 bg-[linear-gradient(transparent_0%,rgba(34,211,238,0.05)_50%,transparent_100%)] bg-[length:100%_4px] animate-[scan_2s_linear_infinite]"></div>

        <div class="relative z-10">
          <div class="text-6xl 2xl:text-8xl mb-6 2xl:mb-10 animate-bounce">✨</div>
          <h2 class="text-cyan-400 font-mono font-black text-2xl 2xl:text-4xl mb-4 2xl:mb-6 tracking-[0.2em] uppercase italic">Identity Initialized</h2>
          <p class="text-white font-mono text-lg 2xl:text-2xl leading-relaxed mb-8 2xl:mb-12 italic opacity-90">"{{ welcomeMessage }}"</p>

          <button @click="showSuccessHologram = false" class="w-full bg-cyan-900/40 hover:bg-cyan-400 text-cyan-400 hover:text-black border border-cyan-400 py-4 2xl:py-6 rounded-xl 2xl:rounded-2xl font-black 2xl:text-xl tracking-widest uppercase transition-all duration-300 shadow-[0_0_15px_rgba(34,211,238,0.2)] hover:shadow-[0_0_30px_rgba(34,211,238,0.6)] group">
            <span class="group-hover:animate-pulse">Enter Databanks</span>
          </button>

        </div>
      </div>
    </div>

    <div v-if="showGradeConfirmModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/80 backdrop-blur-md p-4 transition-all">
      <div class="relative bg-slate-950 rounded-2xl 2xl:rounded-[2rem] shadow-[0_0_50px_-12px_rgba(220,38,38,0.5)] max-w-lg 2xl:max-w-3xl w-full overflow-hidden border border-slate-800 transform scale-100 animate-[fadeIn_0.2s_ease-out]">
        <div class="absolute top-0 left-0 w-full h-1 2xl:h-2 bg-gradient-to-r from-red-600 via-orange-500 to-red-600 animate-pulse"></div>
        <div class="p-6 md:p-8 2xl:p-12 flex items-start gap-5 2xl:gap-8 border-b border-slate-800/60 bg-gradient-to-b from-red-950/20 to-transparent">
          <div class="w-14 h-14 2xl:w-20 2xl:h-20 rounded-xl 2xl:rounded-2xl bg-red-500/10 border border-red-500/20 flex items-center justify-center flex-shrink-0 relative overflow-hidden">
            <div class="absolute inset-0 bg-red-500/20 animate-ping opacity-50"></div>
            <span class="text-3xl 2xl:text-5xl relative z-10">⚠️</span>
          </div>
          <div>
            <h3 class="text-2xl 2xl:text-4xl font-black text-slate-100 tracking-tight uppercase">System Override</h3>
            <div class="flex items-center gap-2 2xl:gap-3 mt-2 2xl:mt-4 text-red-500 font-mono text-[10px] md:text-xs 2xl:text-sm tracking-widest uppercase bg-red-500/10 px-2 py-1 2xl:px-3 2xl:py-1.5 rounded inline-flex">
              <span class="w-2 h-2 2xl:w-3 2xl:h-3 rounded-full bg-red-500 animate-pulse"></span>Warning: Premature Termination
            </div>
          </div>
        </div>
        <div class="p-6 md:p-8 2xl:p-12 font-mono text-sm 2xl:text-xl">
          <div class="text-slate-400 mb-6 2xl:mb-10 leading-relaxed space-y-2 2xl:space-y-4">
            <p>> Analyzing current session state...</p>
            <p>> <span class="text-amber-400">Warning: Unresolved parameters detected.</span></p>
            <p>> Halting the simulation now will result in all uncommitted queries being flagged as <span class="text-red-400 font-bold bg-red-500/10 px-1">CRITICAL_FAILURES</span>.</p>
          </div>
          <div class="bg-slate-900 border border-slate-800 p-4 2xl:p-6 rounded-lg 2xl:rounded-xl">
            <p class="text-slate-300 font-bold tracking-wide">Execute final telemetry calculation anyway?</p>
          </div>
        </div>
        <div class="bg-slate-950 p-6 md:p-8 2xl:p-12 border-t border-slate-800 flex flex-col md:flex-row justify-end gap-4 2xl:gap-6">
          <button @click="showGradeConfirmModal = false" class="px-6 py-3 2xl:px-8 2xl:py-4 rounded-xl 2xl:rounded-2xl font-mono text-sm 2xl:text-lg font-bold text-slate-400 hover:text-white hover:bg-slate-800 transition-colors border border-transparent hover:border-slate-700">[ ESC ] ABORT</button>
          <button @click="executeGradingSequence" class="group relative px-8 py-3 2xl:px-10 2xl:py-4 rounded-xl 2xl:rounded-2xl font-mono text-sm 2xl:text-lg font-black text-red-100 bg-red-600 overflow-hidden shadow-[0_0_20px_rgba(220,38,38,0.4)] hover:shadow-[0_0_30px_rgba(220,38,38,0.6)] transition-all">
            <div class="absolute inset-0 bg-white/20 translate-y-full group-hover:translate-y-0 transition-transform duration-300 ease-in-out"></div>
            <span class="relative z-10">> EXECUTE_GRADE</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="showPremiumModal" class="fixed inset-0 z-50 flex items-start md:items-center justify-center p-4 overflow-y-auto">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" @click="showPremiumModal = false"></div>

      <div class="relative bg-white rounded-3xl 2xl:rounded-[3rem] shadow-2xl w-full max-w-3xl 2xl:max-w-4xl animate-fade-in-up flex flex-col md:flex-row my-8">

        <div class="bg-slate-900 p-6 md:p-8 2xl:p-12 text-white md:w-2/5 flex flex-col justify-center relative overflow-hidden">
          <div class="absolute top-0 right-0 w-64 h-64 2xl:w-96 2xl:h-96 bg-indigo-500/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>

          <h2 class="text-2xl md:text-3xl 2xl:text-4xl font-black mb-3 2xl:mb-6 leading-tight">
            Unlock the <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-purple-400">Knowledge Forge</span>
          </h2>
          <p class="text-slate-400 font-medium mb-6 2xl:mb-8 text-sm 2xl:text-base">
            Stop guessing. Get unlimited exam simulations, all game modes, and the full study material library to dominate your AWS certification.
          </p>

          <ul class="space-y-4 2xl:space-y-6 font-medium text-sm 2xl:text-xl text-slate-300">
            <li class="flex items-center gap-3 2xl:gap-5"><span class="flex h-6 w-6 2xl:h-8 2xl:w-8 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs 2xl:text-sm">✓</span> Unlimited Exam Simulations</li>
            <li class="flex items-center gap-3 2xl:gap-5"><span class="flex h-6 w-6 2xl:h-8 2xl:w-8 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs 2xl:text-sm">✓</span> All Game Modes (AMRAP, Flashcards)</li>
            <li class="flex items-center gap-3 2xl:gap-5"><span class="flex h-6 w-6 2xl:h-8 2xl:w-8 rounded-full bg-emerald-500/20 text-emerald-400 items-center justify-center text-xs 2xl:text-sm">✓</span> Full Study Material Library</li>
          </ul>
          <div v-if="founderSpotsActive" class="mt-6 bg-amber-500/20 border border-amber-500/40 rounded-xl p-4 text-center">
            <p class="text-amber-300 font-black text-xs uppercase tracking-widest mb-1">🔥 Founding Member Offer</p>
            <p class="text-white font-black text-2xl">{{ founderSpotsRemaining }} <span class="text-sm font-normal opacity-70">spots remaining</span></p>
            <p class="text-amber-200 text-xs mt-1 opacity-80">Lock in your discount forever</p>
          </div>
        </div>

        <div class="p-6 md:p-8 2xl:p-12 pt-[5.5rem] md:pt-24 2xl:pt-28 md:w-3/5 bg-slate-50 flex flex-col justify-center gap-4 2xl:gap-6 overflow-visible rounded-r-3xl 2xl:rounded-r-[3rem]">
          <button @click="showPremiumModal = false" class="absolute top-4 right-4 2xl:top-6 2xl:right-6 text-slate-400 hover:text-slate-600 transition-colors z-10">
            <span class="text-2xl 2xl:text-3xl font-black">×</span>
          </button>

          <div class="relative bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border-2 border-indigo-500 shadow-[0_0_30px_rgba(99,102,241,0.15)] transition-all" :class="isCheckoutLoading ? 'opacity-50 pointer-events-none' : 'hover:-translate-y-1'">
            <div class="absolute -top-3 2xl:-top-4 left-1/2 -translate-x-1/2 bg-indigo-500 text-white text-[10px] 2xl:text-sm font-black uppercase tracking-widest px-3 py-1 2xl:px-5 2xl:py-2 rounded-full">
              Most Popular
            </div>
            <div class="flex justify-between items-end mb-2 2xl:mb-4">
              <h3 class="text-lg 2xl:text-3xl font-black text-slate-800">7-Day Trial</h3>
              <div class="text-right">
                <span class="text-3xl 2xl:text-5xl font-black text-indigo-600">$1</span>
              </div>
            </div>
            <p class="text-sm 2xl:text-xl text-slate-500 font-medium mb-4 2xl:mb-6">Test the waters. Renews at $9.99/mo. Cancel anytime.</p>

            <button
                @click="prepareCheckout('trial')"
                :disabled="isCheckoutLoading"
                class="w-full py-3 2xl:py-5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl 2xl:rounded-2xl font-bold 2xl:text-xl tracking-wide transition-all shadow-md hover:shadow-indigo-500/30 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
    <span v-if="isCheckoutLoading" class="flex items-center gap-2">
      <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
      Connecting...
    </span>
              <span v-else>Start $1 Trial</span>
            </button>
          </div>

          <div class="bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border border-slate-200 transition-all" :class="isCheckoutLoading ? 'opacity-50 pointer-events-none' : 'hover:-translate-y-1 hover:border-slate-300 hover:shadow-lg'">
            <div class="flex justify-between items-end mb-2 2xl:mb-4">
              <h3 class="text-lg 2xl:text-3xl font-bold text-slate-700">Monthly Pro</h3>
              <div class="text-right">
                <div v-if="founderSpotsActive">
                  <span class="text-sm line-through text-slate-400 mr-1">$9.99</span>
                  <span class="text-2xl 2xl:text-4xl font-black text-slate-800">$6.99</span>
                  <span class="text-xs 2xl:text-lg text-slate-400 font-bold">/mo</span>
                </div>
                <div v-else>
                  <span class="text-2xl 2xl:text-4xl font-black text-slate-800">$9.99</span>
                  <span class="text-xs 2xl:text-lg text-slate-400 font-bold">/mo</span>
                </div>
              </div>
            </div>
            <p v-if="founderSpotsActive" class="text-sm 2xl:text-xl text-orange-500 font-bold mb-1">🔥 Founding Member — 30% off forever!</p>
            <p class="text-sm 2xl:text-xl text-slate-500 font-medium mb-4 2xl:mb-6">Jump straight into the deep end. Cancel anytime.</p>

            <button
                @click="prepareCheckout('monthly')"
                :disabled="isCheckoutLoading"
                class="w-full py-3 2xl:py-5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl 2xl:rounded-2xl font-bold 2xl:text-xl tracking-wide transition-all shadow-md hover:shadow-indigo-500/30 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
    <span v-if="isCheckoutLoading" class="flex items-center gap-2">
      <svg class="animate-spin h-5 w-5 text-slate-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
      Connecting...
    </span>
              <span v-else>Subscribe Monthly</span>
            </button>
          </div>

          <div class="relative bg-white rounded-2xl 2xl:rounded-3xl p-6 2xl:p-10 border border-emerald-200 transition-all relative" :class="isCheckoutLoading ? 'opacity-50 pointer-events-none' : 'hover:-translate-y-1 hover:border-emerald-400 hover:shadow-lg'">
            <div class="absolute -top-3 2xl:-top-4 right-4 2xl:right-6 bg-emerald-500 text-white text-[10px] 2xl:text-sm font-black uppercase tracking-widest px-3 py-1 2xl:px-5 2xl:py-2 rounded-full">
              Save 33%
            </div>
            <div class="flex justify-between items-end mb-2 2xl:mb-4">
              <h3 class="text-lg 2xl:text-3xl font-bold text-slate-700">Annual Plan</h3>
              <div class="text-right">
                <div v-if="founderSpotsActive">
                  <span class="text-sm line-through text-slate-400 mr-1">$79.99</span>
                  <span class="text-2xl 2xl:text-4xl font-black text-emerald-600">$39.99</span>
                  <span class="text-xs 2xl:text-lg text-slate-400 font-bold">/yr</span>
                </div>
                <div v-else>
                  <span class="text-2xl 2xl:text-4xl font-black text-emerald-600">$79.99</span>
                  <span class="text-xs 2xl:text-lg text-slate-400 font-bold">/yr</span>
                </div>
              </div>
            </div>
            <p v-if="founderSpotsActive" class="text-sm 2xl:text-xl text-emerald-600 font-bold mb-1">🔥 Founding Member — 50% off forever!</p>
            <p class="text-sm 2xl:text-xl text-slate-500 font-medium mb-4 2xl:mb-6">Best value. Cancel anytime.</p>

            <button
                @click="prepareCheckout('annual')"
                :disabled="isCheckoutLoading"
                class="w-full py-3 2xl:py-5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl 2xl:rounded-2xl font-bold 2xl:text-xl tracking-wide transition-all shadow-md hover:shadow-indigo-500/30 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
    <span v-if="isCheckoutLoading" class="flex items-center gap-2">
      <svg class="animate-spin h-5 w-5 text-emerald-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
      Connecting...
    </span>
              <span v-else>Subscribe Annually</span>
            </button>
          </div>

        </div>
      </div>
    </div>

    <div v-if="showUpgradePrompt" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/80 backdrop-blur-sm animate-fade-in">
      <div class="bg-white rounded-3xl 2xl:rounded-[3rem] p-8 2xl:p-16 max-w-md 2xl:max-w-2xl w-full shadow-2xl relative border border-slate-100 text-center animate-fade-in-up">

        <div class="w-20 h-20 2xl:w-32 2xl:h-32 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center text-4xl 2xl:text-6xl mx-auto mb-6 2xl:mb-10 shadow-inner">
          🚀
        </div>

        <h2 class="text-2xl 2xl:text-4xl font-black text-slate-800 mb-3 2xl:mb-6">Unlock Knowledge Forge</h2>

        <p class="text-slate-600 mb-8 2xl:mb-12 font-medium 2xl:text-xl leading-relaxed">
          Premium access is required to enter the Forge. Start your 7-day trial for just <span class="font-black text-indigo-600 text-lg 2xl:text-2xl">$1</span>, then $9.99/mo. Cancel anytime.
        </p>

        <div class="flex gap-4 2xl:gap-6 mt-4">
          <button @click="showUpgradePrompt = false" class="flex-1 py-3.5 2xl:py-5 2xl:text-xl bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-xl 2xl:rounded-2xl font-bold transition-colors">
            Cancel
          </button>

          <button @click="handleSignUpClick" class="flex-1 py-3.5 2xl:py-5 2xl:text-xl bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl 2xl:rounded-2xl font-bold shadow-md shadow-indigo-500/30 transition-all hover:-translate-y-0.5">
            Sign Up
          </button>
        </div>

      </div>
    </div>

    <div v-if="showAlreadyPremiumModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/80 backdrop-blur-sm">
      <div class="bg-white rounded-3xl 2xl:rounded-[3rem] p-8 2xl:p-16 max-w-md 2xl:max-w-2xl w-full shadow-2xl border border-slate-100 text-center animate-fade-in-up">

        <div class="text-6xl 2xl:text-8xl mb-4 2xl:mb-8">🌟</div>
        <h3 class="text-2xl 2xl:text-4xl font-bold text-slate-800 mb-2 2xl:mb-4">You're Already Premium!</h3>
        <p class="text-slate-500 mb-8 2xl:mb-12 2xl:text-xl leading-relaxed">
          Your account is already fully upgraded. You have unrestricted access to the Knowledge Forge and all Premium features.
        </p>

        <div class="flex flex-col space-y-3 2xl:space-y-6">
          <button
              @click="showAlreadyPremiumModal = false"
              class="w-full bg-blue-600 text-white font-bold py-3 px-4 2xl:py-5 2xl:text-xl rounded-xl 2xl:rounded-2xl hover:bg-blue-700 transition-all duration-200 transform hover:-translate-y-0.5 shadow-lg shadow-blue-500/30"
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
              class="w-full bg-slate-100 text-slate-600 font-bold py-3 px-4 2xl:py-5 2xl:text-xl rounded-xl 2xl:rounded-2xl hover:bg-slate-200 transition-colors"
          >
            Log Out / Switch Account
          </button>
        </div>

      </div>
    </div>

    <div v-if="showWelcomeModal" class="fixed inset-0 bg-slate-900/95 backdrop-blur-md flex items-center justify-center z-[100] p-4 transition-all">
      <div class="bg-slate-800 border-2 border-blue-500 rounded-[2.5rem] p-10 text-center shadow-[0_0_60px_rgba(59,130,246,0.3)] animate-fade-in w-full max-w-md">

        <div class="text-6xl mb-6 animate-bounce">⚔️</div>

        <h2 class="text-3xl font-black text-white mb-2 tracking-tight">Welcome to the Forge,</h2>
        <h3 class="text-4xl font-black text-blue-400 mb-8 uppercase tracking-widest">{{ authForm.username }}</h3>

        <div class="flex items-center justify-center gap-3 text-slate-400 font-medium bg-slate-900/50 p-4 rounded-xl">
          <div class="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          Forging your databanks...
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

  <div v-if="showCancelModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-3xl w-full max-w-2xl p-8 md:p-12 shadow-2xl relative">
      <button @click="showCancelModal = false" class="absolute top-6 right-6 text-slate-400 hover:text-slate-700 font-bold text-xl">✕</button>

      <h2 class="text-3xl font-black text-slate-900 mb-2">Cancel Premium</h2>
      <p class="text-slate-500 font-medium mb-6">We're sorry to see you go. Please review the terms of cancellation below.</p>

      <div class="bg-slate-50 border border-slate-200 rounded-xl p-4 h-48 overflow-y-auto mb-6 text-xs text-slate-600 leading-relaxed font-mono">
        <p class="mb-2 font-bold uppercase">1. No Prorated Refunds</p>
        <p class="mb-4">By initiating this cancellation, you acknowledge that Knowledge Forge Academy operates on a strict no-prorated-refund policy. Your Premium Membership will remain fully active until the end of your current billing cycle, at which point your account will be downgraded to the Free tier.</p>

        <p class="mb-2 font-bold uppercase">2. Limitation of Liability</p>
        <p class="mb-4">Knowledge Forge Academy provides educational materials "as is". We make no warranties, express or implied, regarding the outcome of any AWS Certification exams. By accepting these terms, you waive the right to hold Knowledge Forge Academy liable for any exam failures, loss of employment, or related damages.</p>

        <p class="mb-2 font-bold uppercase">3. Class Action Waiver</p>
        <p>You agree to resolve any disputes with Knowledge Forge Academy on an individual basis. You waive the right to participate in any class, consolidated, or representative action.</p>
      </div>

      <label class="flex items-start gap-3 cursor-pointer mb-8 p-4 border border-red-100 bg-red-50 rounded-xl hover:bg-red-100 transition-colors">
        <input type="checkbox" v-model="legalAgreed" class="mt-1 w-5 h-5 accent-red-500 cursor-pointer" />
        <span class="text-sm font-bold text-red-900">I have read and agree to the Limitation of Liability, and I understand my membership will remain active until the end of the term with no refunds.</span>
      </label>

      <p v-if="cancelFeedback" class="text-center font-bold mb-4" :class="cancelFeedback.includes('✅') ? 'text-emerald-600' : 'text-red-500'">
        {{ cancelFeedback }}
      </p>

      <button
          @click="processCancellation"
          :disabled="!legalAgreed"
          class="w-full py-4 rounded-xl font-black text-lg transition-all"
          :class="legalAgreed ? 'bg-red-600 hover:bg-red-700 text-white shadow-lg' : 'bg-slate-200 text-slate-400 cursor-not-allowed'"
      >
        CONFIRM CANCELLATION
      </button>
    </div>
  </div>

  <div v-if="showRatingModal" class="fixed inset-0 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center z-[100] p-4 animate-fade-in">
    <div class="bg-white rounded-[2rem] w-full max-w-lg p-8 md:p-10 shadow-2xl relative border border-slate-200">
      <button @click="showRatingModal = false" class="absolute top-6 right-6 text-slate-400 hover:text-slate-700 font-bold text-xl transition-colors">✕</button>

      <h2 class="text-3xl font-black text-slate-900 mb-2">Rate Your Training</h2>
      <p class="text-slate-500 font-medium mb-8">How is the Knowledge Forge preparing you for the exam?</p>

      <div class="flex gap-2 justify-center mb-8">
        <button
            v-for="star in 5" :key="star"
            @click="reviewForm.rating = star"
            class="text-4xl transition-all hover:scale-110"
            :class="star <= reviewForm.rating ? 'text-amber-400 drop-shadow-md' : 'text-slate-200 grayscale'"
        >
          ⭐
        </button>
      </div>

      <select
          v-model="reviewForm.jobTitle"
          class="w-full bg-slate-50 border-2 border-slate-200 text-slate-700 font-bold rounded-xl p-4 mb-4 focus:outline-none focus:border-indigo-500 transition-colors cursor-pointer"
      >
        <option value="Cloud Enthusiast">Cloud Enthusiast</option>
        <option value="Cloud Practitioner">Cloud Practitioner</option>
        <option value="Solutions Architect">Solutions Architect</option>
        <option value="DevOps Engineer">DevOps Engineer</option>
        <option value="Data Engineer">Data Engineer</option>
        <option value="Security Specialist">Security Specialist</option>
        <option value="Software Developer">Software Developer</option>
      </select>

      <textarea
          v-model="reviewForm.reviewText"
          rows="4"
          placeholder="Tell us about your experience..."
          class="w-full bg-slate-50 border-2 border-slate-200 text-slate-700 rounded-xl p-4 focus:outline-none focus:border-indigo-500 transition-colors resize-none mb-6 font-medium"
      ></textarea>

      <p v-if="reviewFeedbackMessage" class="text-center font-bold mb-4" :class="reviewFeedbackMessage.includes('✅') ? 'text-emerald-600' : 'text-red-500'">
        {{ reviewFeedbackMessage }}
      </p>

      <button
          @click="submitUserReview"
          class="w-full py-4 rounded-xl font-black text-lg transition-all bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg hover:shadow-indigo-500/30 hover:-translate-y-0.5"
      >
        SUBMIT DATABANK REVIEW
      </button>
    </div>
  </div>

</template>

<script setup>
  import { ref, onMounted, computed, watch, onUnmounted, nextTick } from 'vue';
  import { jsPDF } from "jspdf";
  import autoTable from 'jspdf-autotable';
  import { Radar } from 'vue-chartjs';
  import { Chart as ChartJS, RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, DoughnutController, BarController } from 'chart.js';
  import ArchitectureMatrix from './ArchitectureMatrix.vue';
  import StudyMaterialView from './StudyMaterialView.vue';

  // Register all Chart.js components we need
  ChartJS.register(RadialLinearScale, PointElement, LineElement, Filler, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, DoughnutController, BarController);

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
          // 🚨 THE SHIELD: Is the local user already premium?
          const isLocallyPremium = currentUser.value ? currentUser.value.isPremium : false;

          currentUser.value = {
            ...currentUser.value,
            username: username,
            // 🚨 THE FIX: If local says True, ignore the stale False prop!
            isPremium: isLocallyPremium ? true : isPremium
          };
        } else if (!isLoggedIn) {
          // 🚨 THE MASTER SHIELD: App.vue thinks we are logged out, but ARE WE?
          // Check the browser's permanent memory before destroying the user!
          const actualToken = localStorage.getItem('aws_jwt');

          if (!actualToken) {
            currentUser.value = null; // Only wipe if the token is ACTUALLY gone!
          } else {
            // If the token exists, ignore the parent's 'false' prop!
            console.log("🛡️ Master Shield activated: Blocked an unwarranted logout request from App.vue.");
          }
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
  );

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

    // 📊 Fetch personal analytics when user logs in
    if (newUser && newUser.username) {
      fetchUserStats(newUser.username);
      // Load streak from profile
      fetch(`${API_BASE_URL}/api/users/profile`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('aws_jwt')}` }
      }).then(r => r.ok ? r.json() : null).then(data => {
        if (data && data.currentStreak !== undefined) userStreak.value = data.currentStreak;
      }).catch(() => {});
    } else {
      userStats.value = null;
    }
  });

// 📊 Fetch user's personal analytics
const fetchUserStats = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/analytics/user/${encodeURIComponent(username)}`);
    if (response.ok) {
      const data = await response.json();
      // Derive top mode and top cert for display
      const modes = data.gameModeBreakdown || {};
      const topModeEntry = Object.entries(modes).sort((a, b) => b[1] - a[1])[0];
      data.topMode = topModeEntry && topModeEntry[1] > 0 ? topModeEntry[0] : '-';

      const certs = data.certEngagement || {};
      const topCertEntry = Object.entries(certs).sort((a, b) => b[1] - a[1])[0];
      data.topCert = topCertEntry ? topCertEntry[0] : '-';

      userStats.value = data;
    }
  } catch (e) { /* silent fail */ }
};

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
const aiTutorResponse = ref('');
const aiTutorLoading = ref(false);
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
const token = localStorage.getItem('aws_jwt');

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
const showAgreementModal = ref(false);
const hasAcceptedTerms = ref(false);
const pendingPlanType = ref(null);
const pipelineFile = ref(null);
const studyFile = ref(null);
const studyUploadStatus = ref('');
const studyPasteForm = ref({ certCode: '', domain: '', title: '', content: '' });
const studyPasteStatus = ref('');
const adminActiveTab = ref('questions');

// --- Platform State ---
const currentView = ref(window.location.hash.replace('#', '') || 'landing');
// --- 🚀 ONBOARDING PROTOCOL STATE ---
const activeSlide = ref(0);
const selectedCert = ref(null);
const showGuestLimitModal = ref(false);
const hallOfFame = ref([]);
const isCheckoutLoading = ref(false);
const checkoutError = ref('');
const studyActiveCert = ref('');
const studyActiveDomain = ref('');

// --- 🔔 TOAST NOTIFICATION STATE ---
const toastMessage = ref('');
const toastType = ref('error'); // 'error', 'warning', 'success', 'info'
const toastVisible = ref(false);
let toastTimer = null;

const showToast = (message, type = 'error', duration = 4000) => {
  toastMessage.value = message;
  toastType.value = type;
  toastVisible.value = true;
  if (toastTimer) clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { toastVisible.value = false; }, duration);
};

// 📊 ANALYTICS: Track user engagement events
const trackEvent = (eventType, certCode = null, detail = null) => {
  const username = currentUser.value?.username || 'guest';
  fetch(`${API_BASE_URL}/api/analytics/event`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, eventType, certCode, detail })
  }).catch(() => {}); // Fire and forget
};

// � AI TUTOR: Ask Bedrock for a personalized explanation
const askAiTutor = async () => {
  if (!question.value) return;
  aiTutorLoading.value = true;
  aiTutorResponse.value = '';

  const userAnswer = question.value.options
    .filter(o => selectedAnswers.value.includes(o.id))
    .map(o => o.text || o.optionText || o.value)
    .join(', ');

  const correctAnswer = question.value.options
    .filter(o => o.isCorrect === true || o.correct === true)
    .map(o => o.text || o.optionText || o.value)
    .join(', ');

  try {
    const response = await fetch('https://2zm5xtvh2l.execute-api.us-east-1.amazonaws.com/tutor', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        questionText: question.value.questionText,
        userAnswer: userAnswer,
        correctAnswer: correctAnswer,
        explanation: question.value.explanation,
        certCode: selectedCert.value?.code || 'AWS'
      })
    });

    if (response.ok) {
      const data = await response.json();
      aiTutorResponse.value = data.response;
    } else {
      aiTutorResponse.value = 'AI Tutor is currently offline. Please review the explanation above.';
    }
  } catch (error) {
    aiTutorResponse.value = 'Connection failed. AI Tutor unavailable.';
  } finally {
    aiTutorLoading.value = false;
  }
};

// --- 🧠 THE KNOWLEDGE FORGE STATE ---
const activeForgeTab = ref('domains'); // 'domains', 'videos', or 'cheatsheets'
const expandedDomain = ref(null); // Tracks which accordion is currently open

// --- 📊 USER ANALYTICS STATE ---
const userStats = ref(null);

// --- 🏆 FOUNDER DISCOUNT STATE ---
const founderSpotsRemaining = ref(100);
const founderSpotsActive = ref(true);
const showFounderBanner = ref(false);

// --- 📱 PWA INSTALL ---
const pwaInstallPrompt = ref(null);
const showPwaButton = ref(false);

if (typeof window !== 'undefined') {
  window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    pwaInstallPrompt.value = e;
    showPwaButton.value = true;
  });
}

const installPwa = async () => {
  if (!pwaInstallPrompt.value) return;
  pwaInstallPrompt.value.prompt();
  const result = await pwaInstallPrompt.value.userChoice;
  if (result.outcome === 'accepted') showPwaButton.value = false;
};

const fetchFounderSpots = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/payment/founder-spots`);
    if (response.ok) {
      const data = await response.json();
      founderSpotsRemaining.value = data.spotsRemaining;
      founderSpotsActive.value = data.active;
      showFounderBanner.value = data.active;
    }
  } catch (e) { /* silent fail */ }
};

// --- 📊 ADMIN ANALYTICS STATE ---
const adminAnalytics = ref({});
const gameModeChartRef = ref(null);
const certPopChartRef = ref(null);
const engagementSplitChartRef = ref(null);
let gameModeChartInstance = null;
let certPopChartInstance = null;
let engagementSplitChartInstance = null;

const loadAdminAnalytics = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/analytics/admin/summary`);
    if (!response.ok) return;
    const data = await response.json();
    adminAnalytics.value = data;

    // Derive top mode and top cert
    const modes = data.gameModePopularity || {};
    const topModeEntry = Object.entries(modes).sort((a, b) => b[1] - a[1])[0];
    adminAnalytics.value.topMode = topModeEntry ? topModeEntry[0] : '-';

    const certs = data.certPopularity || {};
    const topCertEntry = Object.entries(certs).sort((a, b) => b[1] - a[1])[0];
    adminAnalytics.value.topCert = topCertEntry ? topCertEntry[0] : '-';

    // Render charts after DOM updates
    await nextTick();
    renderAdminCharts(data);
  } catch (e) { console.error('Analytics fetch failed:', e); }
};

const renderAdminCharts = (data) => {
  // Destroy existing charts
  if (gameModeChartInstance) gameModeChartInstance.destroy();
  if (certPopChartInstance) certPopChartInstance.destroy();
  if (engagementSplitChartInstance) engagementSplitChartInstance.destroy();

  const modeColors = ['#6366f1', '#ef4444', '#a855f7', '#ec4899'];
  const certColors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#f97316', '#84cc16', '#e11d48', '#14b8a6', '#6366f1', '#d946ef'];

  // Game Mode Bar Chart
  if (gameModeChartRef.value) {
    const modes = data.gameModePopularity || {};
    const labels = Object.keys(modes).map(m => m.replace('_', ' ').toUpperCase());
    const values = Object.values(modes);
    gameModeChartInstance = new ChartJS(gameModeChartRef.value, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Sessions',
          data: values,
          backgroundColor: modeColors.slice(0, labels.length),
          borderRadius: 8,
          borderSkipped: false
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  }

  // Cert Popularity Bar Chart
  if (certPopChartRef.value) {
    const certs = data.certPopularity || {};
    const labels = Object.keys(certs);
    const values = Object.values(certs);
    certPopChartInstance = new ChartJS(certPopChartRef.value, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Engagements',
          data: values,
          backgroundColor: certColors.slice(0, labels.length),
          borderRadius: 8,
          borderSkipped: false
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
      }
    });
  }

  // Engagement Split Doughnut
  if (engagementSplitChartRef.value) {
    const gameSessions = data.totalGameSessions || 0;
    const studyViews = data.totalStudyViews || 0;
    engagementSplitChartInstance = new ChartJS(engagementSplitChartRef.value, {
      type: 'doughnut',
      data: {
        labels: ['Game Sessions', 'Study Material Views'],
        datasets: [{
          data: [gameSessions, studyViews],
          backgroundColor: ['#6366f1', '#10b981'],
          borderWidth: 0,
          spacing: 4
        }]
      },
      options: {
        responsive: true,
        cutout: '60%',
        plugins: {
          legend: { position: 'bottom', labels: { font: { weight: 'bold' }, padding: 20 } }
        }
      }
    });
  }
};

// --- 🎴 FLASHCARD MATRIX STATE ---
const allFlashcards = ref([]);
const currentFlashcardRound = ref(0);
const displayTerms = ref([]);
const displayDefs = ref([]);
const selectedTerm = ref(null);
const selectedDef = ref(null);
const matchedPairs = ref([]);
const selectedFlashcardCsv = ref(null);
const flashcardUploadStatus = ref('');
const allAdminFlashcards = ref([]);
const flashcardAttempts = ref(0);         // Every pair they click counts as 1 attempt
const flashcardMisses = ref({});          // A dictionary of terms they fumbled
const isFlashcardGameComplete = ref(false); // Triggers the final Results screen

// --- 🎴 STUDY CARDS (LIBRARY) STATE ---
const librarySelectedDomain = ref('All');
const startWithTerm = ref(false);
const libraryFlashcards = ref([]);

const flashcardFinalScore = ref(0);

// --- Timer Logic ---
const timer = ref(0);

// JWT AUTHENTICATION ENGINE
const showAuthModal = ref(false);
const liveUserScores = ref([0, 0, 0, 0]);
const showPremiumModal = ref(false);
const isLoginMode = ref(true); // Toggle between Login and Register
const authForm = ref({ username: '', email: '', confirmEmail: '', password: '',
  confirmPassword: '' });
const authError = ref('');
const authToken = ref(localStorage.getItem('aws_jwt') || null); // Load saved token if it exists
const authValidationMessage = ref("");
const showWelcomeModal = ref(false);
const reviewForm = ref({ rating: 5, reviewText: '', jobTitle: 'Cloud Enthusiast' });
const reviewFeedbackMessage = ref('');

// --- Category Logic ---
const selectedCategory = ref('All');
const categories = ['All', 'Data Engineering', 'Algorithms', 'Modeling', 'Deployment', 'Security', 'AI Services'];

// --- Exam History ---
const examHistory = ref([]);

// --- User Login ---
const isSignup = ref(true);
const userResults = ref({});
const recentReviews = ref([]);

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
const allAdminPipelines = ref([]);
const showAdminModal = ref(false);
const isAdminEditing = ref(false);

// --- Review & Print ---
const showReviewModal = ref(false);
const showRatingModal = ref(false);

const allExamQuestions = ref([]);
const allFetchedFlashcards = ref([]);

// --- The functions start here

const showSuccessHologram = ref(false);
const welcomeMessage = ref('');
const lastAttemptId = ref(null);
const selectedCsvFile = ref(null);
const uploadStatus = ref('');
const pipelineUploadStatus = ref('');

// --- THE KNOWLEDGE FORGE: DYNAMIC DATABASE FETCH ---
const libraryModules = ref([]);

// Footer & Cancellation State
const showCancelModal = ref(false);
const showAboutModal = ref(false);
const legalAgreed = ref(false);
const cancelFeedback = ref("");

// --- DYNAMIC TARGETED FETCH ---
const fetchKnowledgeForge = async (targetCertCode) => {
    if (!targetCertCode) return;

    try {
      // 1. Fetch the logged-in user's progress history (This route is protected!)
      let completedModules = [];
      if (currentUser.value && currentUser.value.username) {
        const actualToken = localStorage.getItem('aws_jwt');
        const progressResponse = await fetch(`${API_BASE_URL}/api/progress/${currentUser.value.username}`, {
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
      const response = await fetch(`${API_BASE_URL}/api/modules/cert/${targetCertCode}`, {
        method: 'GET',
        headers: {
          // 🚨 Hand Java the VIP badge so it unlocks the databank!
          'Authorization': `Bearer ${actualToken}`,
          'Content-Type': 'application/json'
        }
      });

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

// Safely check if the current user has the premium flag from Stripe
const isPremiumUser = computed(() => {
    return currentUser.value?.isPremium || false;
  });

  const toggleDomain = (index) => {
    // 🚨 THE FREEMIUM LOCK: If it's not the first item (index 0) and they aren't premium, deny access!
    if (index !== 0 && !isPremiumUser.value) {
      console.log("Access Denied: Premium Databank.");
      // Optional: If you have a toast notification system, trigger it here telling them to upgrade!
      return;
    }

    expandedDomain.value = expandedDomain.value === index ? null : index;
  };

// 📚 Mock Data for the UI (You can move this to your Java backend later!)
const forgeDomains = [
    {
      title: "Domain 1: Fundamentals of AI & ML",
      weight: "20%",
      topics: ["Supervised vs. Unsupervised Learning", "Deep Learning vs. Traditional ML", "Key Terminology (Epochs, Weights, Bias)"],
      keyFact: "Supervised learning requires labeled data; Unsupervised looks for hidden patterns in unlabeled data."
    },
    {
      title: "Domain 2: Fundamentals of Generative AI",
      weight: "24%",
      topics: ["Large Language Models (LLMs)", "Foundation Models", "Tokenization & Embeddings", "Prompt Engineering Techniques"],
      keyFact: "Temperature controls randomness in Generative AI. High temperature = creative/random, Low temperature = deterministic/focused."
    },
    {
      title: "Domain 3: Applications of Foundation Models",
      weight: "28%",
      topics: ["Amazon Bedrock", "Amazon Q", "Amazon SageMaker JumpStart", "Choosing the right model for the use case"],
      keyFact: "Amazon Bedrock is a fully managed service that offers a choice of high-performing foundation models via a single API."
    },
    {
      title: "Domain 4: Responsible AI & Security",
      weight: "28%",
      topics: ["Data Privacy in AWS", "Mitigating Bias and Hallucinations", "Amazon Bedrock Guardrails", "Regulatory Compliance"],
      keyFact: "Bedrock Guardrails allow you to implement safeguards across foundation models based on your specific use cases and responsible AI policies."
    }
  ];

// --- Analytics: Radar Chart Configuration ---
// 1. The Official AWS Domain Dictionary
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

  // 🚨 THE FIX 1: Push the entire chart inward so the labels don't hit the walls
  layout: {
    padding: {
      left: 30,
      right: 30,
      top: 20,
      bottom: 20
    }
  },

  scales: {
    r: {
      angleLines: {
        color: 'rgba(0, 0, 0, 0.1)', // Fixed the opacity so the spokes are visible!
        lineWidth: 1
      },
      grid: {
        color: 'rgba(0, 0, 0, 0.1)', // Fixed the opacity here too
        circular: true,
        lineWidth: 1
      },
      pointLabels: {
        font: {
          size: 11, // Dropped slightly from 13 for better ultra-wide scaling
          family: "'Inter', 'Helvetica Neue', sans-serif",
          weight: '800'
        },
        color: '#334155',

        // 🚨 THE FIX 2: The Auto-Wrapper. If a label is longer than 18 characters, stack it!
        callback: function(label) {
          if (label.length > 18) {
            const words = label.split(' ');
            const half = Math.ceil(words.length / 2);
            // Returns an array, which Chart.js automatically renders as multiple lines
            return [words.slice(0, half).join(' '), words.slice(half).join(' ')];
          }
          return label;
        }
      },
      ticks: {
        display: false,
        min: 0,
        max: 100,
        stepSize: 20
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
    showToast("Invalid attempt ID. Cannot load review.", "warning");
    return;
  }

  // --- THE FIX: Wrap both IDs in String() so "15" === "15" works perfectly! ---
  const targetAttempt = examHistory.value.find(a => String(a.id) === String(attemptId));

  if (!targetAttempt) {
    showToast("Attempt not found in your history archives.", "warning");
    return;
  }

  // Get missed question IDs
  const missedIds = targetAttempt.missedQuestionIds || '';

  // Handle Flawless Victories (No missed questions!)
  if (!missedIds || missedIds.trim() === "") {
    showToast("Flawless Victory! Zero missed questions on this attempt.", "success");
    return;
  }

  // Fetch the specific missed questions from Spring Boot
  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/questions/batch?ids=${missedIds}`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    });

    if (!response.ok) throw new Error("Backend refused the batch request.");

    const missedQuestionsData = await response.json();

    // 1. Load the questions into Vue's memory
    reviewQuestions.value = missedQuestionsData;

    // 2. Force them to 'false' so the UI highlights them as missed
    userResults.value = {};
    missedQuestionsData.forEach(q => {
      userResults.value[q.id] = false;
    });

    // 3. Show the review in quiz view with results overlay
    currentView.value = 'quiz';
    showResults.value = true;
    isHistoricalView.value = true;
    showReviewModal.value = true;

  } catch (error) {
    console.error("Historical Review Error:", error);
    showToast("Jedi Archives offline. Could not retrieve past questions.", "error");
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
  seenQuestionIds.value = [];
  lastAttemptId.value = null;

  //Reset the master clocks!
  totalExamSeconds.value = 0;
  currentQuestionSeconds.value = 0;

  // 2. Turn off all the end-of-quiz UI screens
  showResults.value = false;
  showReviewModal.value = false;
  isHistoricalView.value = false;
  showExplanation.value = false;
  feedback.value = '';
  question.value = null;

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
      requiresPremium: false,
      maxFreePlays: null
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

const handlePipelineFileSelect = (event) => {
    pipelineFile.value = event.target.files[0];
  };

// 3. When the user clicks the INJECT button, fire it to Java!
const submitPipelineCsv = async () => {
    if (!pipelineFile.value) {
      pipelineUploadStatus.value = "❌ Please select a pipeline CSV file first.";
      return;
    }

    const formData = new FormData();
    formData.append('file', pipelineFile.value);
    pipelineUploadStatus.value = 'Injecting into mainframes...';

    try {
      // 🚨 Ensure we grab the official token!
      const token = localStorage.getItem('aws_jwt');

      // (Ensure this URL matches your Java upload endpoint)
      const response = await fetch(`${API_BASE_URL}/api/questions/pipeline/upload`, {
        method: 'POST',
        headers: {
          'X-Admin-Key': 'masterkey',
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      const result = await response.json();

      if (response.ok) {
        // ✅ Replaced the ugly alert() with the sleek UI text!
        pipelineUploadStatus.value = "✅ " + result.message;
        pipelineFile.value = null;

        // 🚨 THE FIX: Instantly refresh the table below!
        if (typeof fetchAdminPipelines === 'function') {
          fetchAdminPipelines();
        }
      } else {
        pipelineUploadStatus.value = "❌ " + (result.error || "Upload failed.");
      }
    } catch (error) {
      console.error("Upload failed", error);
      pipelineUploadStatus.value = "❌ Critical Error: Could not reach the Matrix.";
    }
  };

const handleStudyFileSelect = (event) => {
    studyFile.value = event.target.files[0];
  };

// 3. The Injection Function
const submitStudyCsv = async () => {
    if (!studyFile.value) {
      studyUploadStatus.value = "❌ Please select a Study Material CSV.";
      return;
    }

    const formData = new FormData();
    formData.append('file', studyFile.value);
    studyUploadStatus.value = 'Injecting curriculum into mainframes...';

    try {
      const token = localStorage.getItem('aws_jwt');
      const response = await fetch(`${API_BASE_URL}/api/study/upload`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData
      });

      const result = await response.json();

      if (response.ok) {
        studyUploadStatus.value = "✅ " + result.message + ` (${result.count} documents)`;
        studyFile.value = null;
      } else {
        studyUploadStatus.value = "❌ " + (result.error || "Upload failed.");
      }
    } catch (error) {
      studyUploadStatus.value = "❌ Critical Error: Could not reach the Matrix.";
    }
  };

const submitStudyPaste = async () => {
  studyPasteStatus.value = 'Injecting document into the Knowledge Forge...';
  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/study/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      body: JSON.stringify(studyPasteForm.value)
    });
    const result = await response.json();
    if (response.ok) {
      studyPasteStatus.value = "✅ Document injected successfully!";
      studyPasteForm.value = { certCode: '', domain: '', title: '', content: '' };
    } else {
      studyPasteStatus.value = "❌ " + (result.error || "Upload failed.");
    }
  } catch (error) {
    studyPasteStatus.value = "❌ Critical Error: Could not reach the server.";
  }
};

// Function to handle clicking a card
  // Function to handle clicking a card
const openModule = (moduleId) => {
    // 1. Find the exact module they clicked and its index
    const targetIndex = libraryModules.value.findIndex(m => m.id === moduleId);
    const targetModule = libraryModules.value[targetIndex];

    if (!targetModule) return;

    // 2. THE BOUNCER: Is this a locked module?
    // It is locked if it's NOT the first module (index 0) AND they aren't Premium
    const isLocked = targetIndex > 0 && (!currentUser.value || !currentUser.value.isPremium);

    if (isLocked) {
      // 🚨 UI UPGRADE: Use your sleek custom modal instead of an ugly browser alert!
      showPremiumModal.value = true;
      return; // Abort! Do not let them open the video player!
    }

    // 3. If it's the free module OR they are Premium, let them in!
    // (Audio successfully removed)
    activeModule.value = targetModule;
    currentVideoIndex.value = 0;
    currentView.value = 'lessonViewer'; // Opens the Video Player
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

const onboardingSlides = [
    {
      title: "Propel Your Cloud Career",
      text: "Obtaining AWS certifications is the ultimate catalyst for your tech career. Knowledge Forge Academy works tirelessly to provide the most up-to-date training modules, dynamic quizzes, and real-world scenarios. We are constantly evolving our databanks to ensure you are exam-ready.",
      icon: "🚀"
    },
    {
      title: "Certification Databanks",
      text: "Select your target protocol. Whether you are aiming for Cloud Practitioner, Solutions Architect, Developer, or specialized AI/ML certifications, our forges adapt to your specific exam requirements.",
      icon: "🗄️"
    },
    {
      title: "The Skill Radar",
      text: "Stop guessing what to study. Our dynamic Skill Radar tracks your quiz performance across every AWS domain in real-time, instantly highlighting your strengths and generating Target Priorities to patch your weakest architectural blind spots.",
      icon: "🎯"
    },
    {
      title: "Ascended Masters",
      text: "Compete with the elite. The Ascended Masters leaderboard ranks our top engineers. Your rank is dynamically calculated based on your accumulated XP, Sudden Death survival rates, and overall quiz accuracy. Claim your spot at the top.",
      icon: "👑"
    }
  ];

const nextSlide = () => {
    if (activeSlide.value < onboardingSlides.length - 1) {
      activeSlide.value++;
    } else {
      // If it's the last slide, send them to the main dashboard!
      currentView.value = 'landing';
    }
  };

const prevSlide = () => {
    if (activeSlide.value > 0) {
      activeSlide.value--;
    }
  };

// --- 🎯 DYNAMIC SKILL RADAR ENGINE ---

// 1. Track what the user selects in the dropdown
const selectedRadarCert = ref('AIF-C01');

// 2. The Central Database: Maps certs to their specific domains and mock scores
const radarDatabase = {
    'CLF-C02': ['Cloud Concepts', 'Security & Compliance', 'Cloud Tech & Services', 'Billing & Pricing'],
    'SOA-C02': ['Monitoring & Reporting', 'High Availability', 'Deployment & Provisioning', 'Storage & Data', 'Security & Compliance', 'Networking'],
    'DVA-C02': ['Development with AWS', 'Security', 'Deployment', 'Troubleshooting'],
    'SAA-C03': ['Secure Architectures', 'Resilient Architectures', 'High-Performing', 'Cost-Optimized'],
    'SAP-C02': ['Design for Complexity', 'New Solutions', 'Migration Planning', 'Cost Control', 'Continuous Improvement'],
    'DOP-C02': ['SDLC Automation', 'IaC & Config', 'Monitoring & Logging', 'Policies & Standards', 'Incident Response', 'High Availability'],
    'DEA-C01': ['Data Ingestion', 'Data Store Management', 'Data Operations', 'Security & Governance'],
    'MLA-C01': ['Data Preparation', 'ML Architecture', 'Model Training', 'Deployment & Ops', 'Security'],
    'AIP-C01': ['AI/ML Fundamentals', 'Generative AI', 'Foundation Models', 'Responsible AI'],
    'SCS-C02': ['Threat Detection', 'Logging & Monitoring', 'Infrastructure Security', 'IAM', 'Data Protection'],
    'ANS-C01': ['Network Design', 'Network Implementation', 'Network Management', 'Network Security'],
    'AIF-C01': ['Fundamentals of AI & ML', 'Fundamentals of Generative AI', 'Applications of Foundation Models', 'Responsible AI & Security']
  };

const dynamicRadarData = computed(() => {
  // 🚨 THE FIX: Dynamically read the exact categories and scores directly from your database!
  const labels = Object.keys(categoryScores.value || {});
  const dataPoints = Object.values(categoryScores.value || {});

  // Fallback default shape if they haven't taken an exam yet
  if (labels.length === 0) {
    return {
      labels: ['Compute', 'Storage', 'Database', 'Security'],
      datasets: [{
        label: 'Skill Proficiency (%)',
        backgroundColor: 'rgba(79, 70, 229, 0.2)',
        borderColor: 'rgba(79, 70, 229, 1)',
        data: [0, 0, 0, 0]
      }]
    };
  }

  // Draw the real, dynamic chart
  return {
    labels: labels,
    datasets: [{
      label: 'Skill Proficiency (%)',
      backgroundColor: 'rgba(79, 70, 229, 0.2)',
      borderColor: 'rgba(79, 70, 229, 1)',
      pointBackgroundColor: 'rgba(16, 185, 129, 1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(16, 185, 129, 1)',
      data: dataPoints
    }]
  };
});

// --- COMPLETE MODULE PROGRESSION ---
const completeActiveModule = async () => {
  // 1. Security Check: Are they logged in?
  if (!currentUser.value || !currentUser.value.username) {
    showToast("Guest mode active. Progress not saved to Mainframe.", "warning");
    return;
  }

  try {
    // 2. Send the completion data to Spring Boot
    const response = await fetch(`${API_BASE_URL}/api/progress/complete?username=${currentUser.value.username}&moduleId=${activeModule.value.id}`, {
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
    showToast("Connection to Jedi Archives failed.", "error");
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
      const response = await fetch(`${API_BASE_URL}/api/modules`);
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
      showToast(`Could not locate archives for "${categoryName}". Select a cert first.`, "warning");
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
  // 🚨 THE FIX: Calculate "Time Spent" based on the game mode
  let secondsSpent = totalExamSeconds.value;

  if (isAmrapMode.value) {
    // AMRAP starts at 600 (10 mins). Time spent is 600 minus whatever is left!
    secondsSpent = 600 - amrapTimeLeft.value;
  }

  const m = Math.floor(secondsSpent / 60).toString().padStart(2, '0');
  const s = (secondsSpent % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// 3. The average time per question shown on the Results screen
const formattedAverageTime = computed(() => {
  // 1. Get the correct total time
  let secondsSpent = totalExamSeconds.value;
  if (isAmrapMode.value) {
    secondsSpent = 600 - amrapTimeLeft.value;
  }

  // 2. Get the correct amount of questions actually answered
  const attemptedCount = Object.keys(selectedAnswersRecord.value).length || 1;

  // 3. Do the math
  const avgSeconds = Math.round(secondsSpent / attemptedCount);

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
    const token = localStorage.getItem('aws_jwt'); // Grab the token
    const response = await fetch(`${API_BASE_URL}/api/questions/random?examCode=${selectedCert.value.code}${excludeQuery}`, {
      // 🚨 THE FIX: Add the safe header
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    });

    // 3. If the server returns 204, the bank is completely empty!
    if (response.status === 204 || !response.ok) {

      // If we are in the middle of a quiz and run out of questions, force the grade!
      if (currentView.value === 'quiz') forceGradeExam();
      return;
    }

    const text = await response.text();
    if (!text) return;

    const data = JSON.parse(text);

    // 🚨 THE TRANSLATOR: Convert Java's flat fields into Vue's Option array!
    if (!data.options) {
      const builtOptions = [];
      const correctAnswers = data.correctOption ? data.correctOption.split('||') : [];

      if (data.optionA) builtOptions.push({ id: 'A', text: data.optionA, isCorrect: correctAnswers.includes(data.optionA) });
      if (data.optionB) builtOptions.push({ id: 'B', text: data.optionB, isCorrect: correctAnswers.includes(data.optionB) });
      if (data.optionC) builtOptions.push({ id: 'C', text: data.optionC, isCorrect: correctAnswers.includes(data.optionC) });
      if (data.optionD) builtOptions.push({ id: 'D', text: data.optionD, isCorrect: correctAnswers.includes(data.optionD) });
      if (data.optionE) builtOptions.push({ id: 'E', text: data.optionE, isCorrect: correctAnswers.includes(data.optionE) });
      if (data.optionF) builtOptions.push({ id: 'F', text: data.optionF, isCorrect: correctAnswers.includes(data.optionF) });

      data.options = builtOptions;
    }

    // 4. Assign the newly translated data to the screen
    question.value = data;

    // 5. Log this ID into our memory bank so it never shows up again!
    seenQuestionIds.value.push(data.id);

    // Reset state for the new question
    selectedAnswers.value = [];
    showExplanation.value = false;
    aiTutorResponse.value = '';
    aiTutorLoading.value = false;

  } catch (error) {
    console.error(error);
    showToast("Uplink severed. Backend mainframe is offline.", "error");
  }
};

// --- 🛡️ ADMIN SECURITY PROTOCOL ---
const triggerAdminLock = () => {
    // 1. Throw up the browser's native password prompt
    const authCode = prompt("SECURITY OVERRIDE: Enter Admin Authorization Code");

    // 2. Check the password
    if (authCode === "masterkey") {
      currentView.value = 'admin'; // Grant Access
    } else if (authCode !== null && authCode !== "") {
      showToast("Authorization Denied. Invalid Code.", "error");
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
      const token = localStorage.getItem('aws_jwt'); // Grab the token
      const response = await fetch(`${API_BASE_URL}/api/questions/random?examCode=${codeToFetch}${excludeQuery}`, {
        // 🚨 THE FIX: Add the safe header
        headers: { 'Authorization': token ? `Bearer ${token}` : '' }
      });

      // 3. THE SAFETY NET: Check if they are an absolute beast and exhausted the bank!
      if (response.status === 204 || !response.ok) {
        showToast("Bank Exhausted! You cleared every question for this cert.", "success");
        forceGradeExam(); // End the AMRAP session early and score them!
        return;
      }

      const text = await response.text();
      if (!text) return;

      const data = JSON.parse(text);

      // 🚨 THE TRANSLATOR: Convert Java's flat fields into Vue's Option array!
      if (!data.options) {
        const builtOptions = [];
        const correctAnswers = data.correctOption ? data.correctOption.split('||') : [];

        if (data.optionA) builtOptions.push({ id: 'A', text: data.optionA, isCorrect: correctAnswers.includes(data.optionA) });
        if (data.optionB) builtOptions.push({ id: 'B', text: data.optionB, isCorrect: correctAnswers.includes(data.optionB) });
        if (data.optionC) builtOptions.push({ id: 'C', text: data.optionC, isCorrect: correctAnswers.includes(data.optionC) });
        if (data.optionD) builtOptions.push({ id: 'D', text: data.optionD, isCorrect: correctAnswers.includes(data.optionD) });
        if (data.optionE) builtOptions.push({ id: 'E', text: data.optionE, isCorrect: correctAnswers.includes(data.optionE) });
        if (data.optionF) builtOptions.push({ id: 'F', text: data.optionF, isCorrect: correctAnswers.includes(data.optionF) });

        data.options = builtOptions;
      }

      // 4. Assign the new data and reset the board
      question.value = data;
      seenQuestionIds.value.push(data.id); // Log it so it doesn't repeat
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
      showToast("Could not load next trial. Check your uplink.", "error");
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

    // Set visual feedback for the explanation screen (AUDIO DELETED)
    if (isRight) {
      feedback.value = "✓ CORRECT";
      feedbackClass.value = "bg-green-100 text-green-800 border-2 border-green-200";
    } else {
      feedback.value = "✗ INCORRECT";
      feedbackClass.value = "bg-red-100 text-red-800 border-2 border-red-200";
    }

    // Tell the AMRAP referee to count the rep!
    if (isAmrapMode.value) {
      amrapCorrectCount.value++;
    }

    // --- THE SUDDEN DEATH ENGINE ---
    if (isSuddenDeath.value) {
      if (!isRight) {
        // AUDIO DELETED
        healthPoints.value--;
        console.warn(`DAMAGE TAKEN! HP remaining: ${healthPoints.value}`);

        // Fatal Blow - Terminate the Exam instantly
        if (isDead.value) {
          // Push the fatal question into the array so the Results screen can grade it.
          if (question.value && !questions.value.some(q => q.id === question.value.id)) {
            questions.value.push(question.value);
          }
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
  const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
      ? allQuestionsInSession.value
      : questions.value;

  const updates = [];

  activeQs.forEach(q => {
    const catName = q.domain || q.category || 'General';
    const gotItRight = userResults.value[q.id] === true;

    updates.push({
      category: catName,
      // 🚨 THE FIX: Send both names so Java's parser can't possibly miss it!
      isCorrect: gotItRight,
      correct: gotItRight
    });
  });
  if (currentUser.value && currentUser.value.username) {
    try {
      const actualToken = localStorage.getItem('aws_jwt');
      const response = await fetch(`${API_BASE_URL}/api/progress/${currentUser.value.username}/radar/update`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${actualToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updates)
      });

      if (response.ok) {
        console.log("✅ Mastery data successfully synced to Jedi Archives.");
        await fetchRadarStats();
      }
    } catch (error) {
      console.error("Failed to sync radar stats:", error);
    }
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
    const url = `${API_BASE_URL}/api/progress/award-xp?username=${savedUser.username}&correctCount=${correctCount}&totalQuestions=${totalQuestions}`;

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
    const results = Object.values(userResults.value);
    const attemptedCount = results.length || 1;
    const correctAnswers = results.filter(result => result === true).length;
    const finalDbScore = Math.round((correctAnswers / attemptedCount) * 100);

    let missedIds = [];
    const activeQs = (allQuestionsInSession.value && allQuestionsInSession.value.length > 0)
        ? allQuestionsInSession.value
        : questions.value;

    activeQs.forEach(q => {
      if (userResults.value[q.id] === false) {
        missedIds.push(q.id);
      }
    });

    // Determine weakest category from missed questions
    const missedCategories = {};
    activeQs.forEach(q => {
      const catName = q.domain || q.category || null;
      if (userResults.value[q.id] === false && catName) {
        missedCategories[catName] = (missedCategories[catName] || 0) + 1;
      }
    });
    // Get top 3 weakest areas
    const sortedWeak = Object.entries(missedCategories).sort((a, b) => b[1] - a[1]);
    const top3Weak = sortedWeak.slice(0, 3).map(e => e[0]).join(', ');

    const token = localStorage.getItem('aws_jwt');

    const historyData = {
      user: { id: currentUser.value.id },
      userId: currentUser.value.id,
      examCode: selectedCert.value?.code || 'AWS-CERT',
      scorePercentage: finalDbScore,
      totalQuestions: attemptedCount,
      correctCount: correctAnswers,
      missedQuestionIds: missedIds.join(','),
      weakestCategory: top3Weak || 'Flawless',
      timeSpentSeconds: isAmrapMode.value ? (600 - amrapTimeLeft.value) : totalExamSeconds.value
    };

    const response = await fetch(`${API_BASE_URL}/api/questions/history/save?userId=${currentUser.value.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(historyData)
    });

    // 🚨 THE UNMASKER: If Java refuses to save the exam, tell us why!
    if (!response.ok) {
      const errText = await response.text();
      console.error("JAVA REJECTED EXAM SAVE:", errText);
      showToast("Database rejected the score: " + errText, "error");
    } else {
      console.log("✅ Exam History Saved Successfully!");
      // Instantly update the UI tables so you don't have to refresh
      if (typeof fetchHistory === 'function') fetchHistory();
      if (typeof fetchLeaderboard === 'function') fetchLeaderboard();
    }

  } catch (error) {
    console.error("Failed to save history to database:", error);
  }
};

const fetchHistory = async () => {
  try {
    const token = localStorage.getItem('aws_jwt');

    // 🚨 The Ghost ID Tripwire
    if (!token || !currentUser.value) return;
    if (!currentUser.value.id) {
      console.warn("⚠️ Data Bridge Paused: Awaiting official User ID from Java...");
      return;
    }

    // Direct Data Bridge: Pulling via the official DB ID
    const response = await fetch(`${API_BASE_URL}/api/questions/history?userId=${currentUser.value.id}`, {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.ok) {
      const data = await response.json();

      // 🚨 THE ARMOR: Only accept the data if it is a list!
      if (Array.isArray(data)) {
        examHistory.value = data;
        console.log("📊 RECENT ACTIVITY DATA:", examHistory.value);
        console.log(`📡 EXM HISTORY: Bridge established. ${examHistory.value.length} nodes received.`);
      } else {
        // If Java sends a weird error object, default to an empty array
        examHistory.value = [];
        console.warn("⚠️ Data Bridge warning: Expected an array, but received an object.");
      }
    } else {
      examHistory.value = [];
      console.error("Failed to establish history data bridge. Server returned an error.");
    }

  } catch (error) {
    console.error("Vaporization failure in history databank:", error);
  }
};

watch(examHistory, (newHistory) => {
  // If we have history, recalculate Target Priorities & Radar instantly.
  if (newHistory && newHistory.length > 0) {
    console.log("Telemetry databank updated. Recalculating radar and priorities...");
    analyzeCategoryMastery();
  }
}, { immediate: true });

const adminForm = ref({
    id: null,
    text: '',
    category: '',
    examCode: 'SCS-C02',
    explanation: '',
    // 🚨 THE NEW UPGRADE: An array of objects instead of a single string
    options: [
      { text: '', isCorrect: true },  // Default Option 1
      { text: '', isCorrect: false }, // Default Option 2
      { text: '', isCorrect: false }, // Default Option 3
      { text: '', isCorrect: false }  // Default Option 4
    ]
  });

const addOption = () => {
    if (adminForm.value.options.length < 6) {
      adminForm.value.options.push({ text: '', isCorrect: false });
    }
  };

const removeOption = (index) => {
    if (adminForm.value.options.length > 2) {
      adminForm.value.options.splice(index, 1);
    }
  };

// 1. READ: Download all questions
const fetchAdminQuestions = async () => {
  try {
    const token = localStorage.getItem('aws_jwt');
    if (!token) return;

    const response = await fetch(`${API_BASE_URL}/api/questions/all`, {
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
      ? `${API_BASE_URL}/api/admin/questions/${adminForm.value.id}`
      : `${API_BASE_URL}/api/admin/questions`;

  const method = isAdminEditing.value ? 'PUT' : 'POST';

// 🚨 THE TRANSLATOR: Convert the UI array back into Java's flat format
const correctOpt = adminForm.value.options.find(o => o.isCorrect);

const javaPayload =
    {
    id: adminForm.value.id,
    certCode: adminForm.value.examCode,
    domain: adminForm.value.category,
    questionText: adminForm.value.text,
    optionA: adminForm.value.options[0]?.text || '',
    optionB: adminForm.value.options[1]?.text || '',
    optionC: adminForm.value.options[2]?.text || '',
    optionD: adminForm.value.options[3]?.text || '',
    optionE: adminForm.value.options[4]?.text || '', // 🚨 ADDED
    optionF: adminForm.value.options[5]?.text || '', // 🚨 ADDED
    correctOption: correctOpt ? correctOpt.text : '',
    explanation: adminForm.value.explanation
  };

  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(javaPayload) // 🚨 Send the translated payload!
    });

    if (response.ok) {
      await fetchAdminQuestions(); // Refresh the table!
      showAdminModal.value = false; // Close the modal
    } else {
      console.error("Java rejected the payload.");
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

const fetchReviews = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/questions/public/reviews`);
      if (response.ok) {
        recentReviews.value = await response.json();
      }
    } catch (error) {
      console.error("Could not fetch reviews:", error);
    }
  };

// The trigger to open the reader
const openStudyMaterial = (cert, domain) => {
    studyActiveCert.value = cert;
    studyActiveDomain.value = domain;
    currentView.value = 'study_reader';
    trackEvent('study_material_view', cert, domain);
  };

// 4. UI HELPER: Open the modal for editing or creating
const openAdminModal = (question = null) => {
    if (question) {
      // If editing, we will eventually need to map the Java options here
      adminForm.value = { ...question };
      isAdminEditing.value = true;
    } else {
      // 🚨 UPDATED BLANK FORM: Now includes the default 4 options!
      adminForm.value = {
        id: null,
        text: '',
        category: '',
        examCode: '',
        explanation: '',
        options: [
          { text: '', isCorrect: true },
          { text: '', isCorrect: false },
          { text: '', isCorrect: false },
          { text: '', isCorrect: false }
        ]
      };
      isAdminEditing.value = false;
    }
    showAdminModal.value = true;
  };

// --- FETCH LIFETIME RADAR STATS FROM SPRING BOOT ---
const fetchRadarStats = async () => {
  if (!currentUser.value || !currentUser.value.username) return;

  try {
    const actualToken = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/progress/${currentUser.value.username}/radar`, {
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
const initiateProtocol = (targetView) => {
    const gameConfig = arcadeConfig[targetView];

    if (!gameConfig) {
      currentView.value = targetView;
      return;
    }

    // 🚨 THE FIX: Reliably check the user's premium status!
    const isPremium = currentUser.value && currentUser.value.isPremium;

    // --- GATE 1: THE PREMIUM WALL ---
    if (gameConfig.requiresPremium && !isPremium) {
      localStorage.setItem('aws_intended_protocol', targetView);
      showPremiumModal.value = true;
      return;
    }

    // --- GATE 2: THE 3-STRIKE TRACKER ---
    if (!isPremium && gameConfig.maxFreePlays) {
      const strikes = parseInt(localStorage.getItem(`aws_${targetView}_strikes`) || '0');

      if (strikes >= gameConfig.maxFreePlays) {
        showPremiumModal.value = true;
        return; // Abort!
      }

      localStorage.setItem(`aws_${targetView}_strikes`, (strikes + 1).toString());
      console.log(`🎫 Free play granted for ${gameConfig.name}: Strike ${strikes + 1} of ${gameConfig.maxFreePlays}`);
    }

    // --- VIP ACCESS GRANTED ---
    // 📊 Track the game start
    const eventMap = { standard: 'exam_start', amrap: 'amrap_start', sudden_death: 'sudden_death_start', flashcards: 'flashcard_start' };
    if (eventMap[targetView]) {
      trackEvent(eventMap[targetView], selectedCert.value?.code);
    }

    if (['standard', 'amrap', 'sudden_death', 'flashcards'].includes(targetView)) {
      bootQuizEngine(targetView);
    } else {
      currentView.value = targetView;
      if (targetView === 'library' && selectedCert.value) {
        fetchKnowledgeForge(selectedCert.value.code);
      }
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

const changeForgeTab = (targetTab) => {
    // 1. The VIP Rooms matching your exact Vue IDs
    const premiumTabs = ['study_cards', 'architecture'];

    // 2. Check their ID
    const isPremium = currentUser.value && currentUser.value.isPremium;

    // 3. THE BOUNCER
    if (premiumTabs.includes(targetTab) && !isPremium) {
      showPremiumModal.value = true;
      return; // Abort the tab switch!
    }

    // 4. If they pass, let them in!
    activeForgeTab.value = targetTab;

    // Fetch the flashcards if they enter the study_cards tab
    if (targetTab === 'study_cards' && selectedCert.value) {
      if (typeof fetchLibraryFlashcards === 'function') {
        fetchLibraryFlashcards(selectedCert.value.code);
      }
    }
  };

const fetchFlashcards = async () => {
    const codeToFetch = selectedCert.value?.code;
    if (!codeToFetch) return;

    try {
      const token = localStorage.getItem('aws_jwt');
      const response = await fetch(`${API_BASE_URL}/api/questions/flashcards/game?examCode=${codeToFetch}`, {
        // 🚨 THE FIX: Only send the Bearer text if the token actually exists!
        headers: { 'Authorization': token ? `Bearer ${token}` : '' }
      });

      if (response.ok) {
        const rawCards = await response.json();
        if (rawCards.length === 0) {
          showToast("No flashcards found in the Matrix for this cert.", "warning");
          return;
        }

        // 1. Reset Telemetry for a new game
        flashcardAttempts.value = 0;
        flashcardMisses.value = {};
        isFlashcardGameComplete.value = false;

        // 2. Save all cards and shuffle the master deck
        allFetchedFlashcards.value = rawCards.sort(() => Math.random() - 0.5);

        // 3. Start Round 1
        currentFlashcardRound.value = 0;
        loadNextFlashcardRound();
      }
    } catch (error) {
      console.error("Flashcard API Offline:", error);
    }
  };

const loadNextFlashcardRound = () => {
  matchedPairs.value = [];
  selectedTerm.value = null;
  selectedDef.value = null;

  // 🚨 THE LIMITER: Change this to 5 when you are done testing!
  const MAX_ROUNDS = 1;

  const startIndex = currentFlashcardRound.value * 6;
  const roundCards = allFetchedFlashcards.value.slice(startIndex, startIndex + 6);

  // 🚨 THE TRIGGER: End the game if the database is empty OR they hit the Max Rounds!
  if (roundCards.length === 0 || currentFlashcardRound.value >= MAX_ROUNDS) {

    // 1. Trigger the Results Screen
    isFlashcardGameComplete.value = true;

    // 2. Dynamic Accuracy Math (Only grade them on the cards they actually played)
    const cardsPlayed = Math.min(allFetchedFlashcards.value.length, currentFlashcardRound.value * 6);

    if (flashcardAttempts.value > 0) {
      flashcardFinalScore.value = Math.round((cardsPlayed / flashcardAttempts.value) * 100);
    } else {
      flashcardFinalScore.value = 0; // Failsafe
    }

    // 3. Award XP
    if (currentUser.value) {
      const xpEarned = Math.round(flashcardFinalScore.value * 1.5);
      userTotalXp.value += xpEarned;
    }
    return;
  }

  // If the game isn't over, deal the next cards!
  displayTerms.value = [...roundCards].sort(() => Math.random() - 0.5);
  displayDefs.value = [...roundCards].sort(() => Math.random() - 0.5);
};

const selectTerm = (term) => {
  if (matchedPairs.value.includes(term.id)) return;
  selectedTerm.value = term;
  checkFlashcardMatch();
};

const selectDef = (def) => {
  if (matchedPairs.value.includes(def.id)) return;
  selectedDef.value = def;
  checkFlashcardMatch();
};

const fetchAdminPipelines = async () => {
    try {
      const token = localStorage.getItem('aws_jwt');
      // 🚨 Notice the /admin/ in this URL!
      const response = await fetch(`${API_BASE_URL}/api/admin/questions/pipeline/all`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        allAdminPipelines.value = await response.json();
      }
    } catch (error) {
      console.error("Architecture Matrix Offline:", error);
    }
  };

const fetchAdminFlashcards = async () => {
    try {
      const token = localStorage.getItem('aws_jwt');
      // 🚨 Notice the /admin/ in this URL!
      const response = await fetch(`${API_BASE_URL}/api/admin/questions/flashcards/all`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        allAdminFlashcards.value = await response.json();
      }
    } catch (error) {
      console.error("Flashcard Matrix Offline:", error);
    }
  };

// 3. The function to delete a pipeline
const deleteAdminPipeline = async (id) => {
    if (!confirm("⚠ VAPORIZE PIPELINE? This cannot be undone.")) return;
    try {
      const token = localStorage.getItem('aws_jwt');
      const response = await fetch(`${API_BASE_URL}/api/questions/pipeline/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) fetchAdminPipelines(); // Refresh table!
    } catch (error) {
      console.error("Failed to vaporize pipeline:", error);
    }
  };

const checkFlashcardMatch = () => {
  if (selectedTerm.value && selectedDef.value) {

    // 📈 TELEMETRY: Every time they click a pair, it's 1 attempt!
    flashcardAttempts.value++;

    if (selectedTerm.value.id === selectedDef.value.id) {
      // ✅ MATCH
      matchedPairs.value.push(selectedTerm.value.id);
      selectedTerm.value = null;
      selectedDef.value = null;
    } else {
      // ❌ FAILED MATCH: Log it in the telemetry dictionary!
      const missedId = selectedTerm.value.id;
      if (!flashcardMisses.value[missedId]) {
        // If they haven't missed it before, save the full term and definition
        flashcardMisses.value[missedId] = {
          term: selectedTerm.value.term,
          definition: selectedTerm.value.definition,
          mistakes: 0
        };
      }
      // Add a strike to this specific term
      flashcardMisses.value[missedId].mistakes++;

      setTimeout(() => {
        selectedTerm.value = null;
        selectedDef.value = null;
      }, 600);
    }
  }
};

// 1. Fetch exactly the cards needed for the Library
const fetchLibraryFlashcards = async (certCode) => {
    if (!certCode) return;
    try {
      const token = localStorage.getItem('aws_jwt');
      const response = await fetch(`${API_BASE_URL}/api/questions/flashcards/game?examCode=${certCode}`, {
        // 🚨 THE FIX: Safely handle guests!
        headers: { 'Authorization': token ? `Bearer ${token}` : '' }
      });

      if (response.ok) {
        const rawCards = await response.json();
        console.log("🃏 FLASHCARDS FROM JAVA:", rawCards);

        // 🚨 Inject the reactive 'flipped' variable into every card so they can flip individually!
        libraryFlashcards.value = rawCards.map(card => ({
          ...card,
          flipped: false
        }));
      } else {
        libraryFlashcards.value = [];
      }
    } catch (error) {
      console.error("Failed to load library flashcards:", error);
    }
  };

// 2. Automatically extract unique domains to build the dropdown menu
const libraryFlashcardDomains = computed(() => {
  const domains = new Set();
  libraryFlashcards.value.forEach(card => {
    if (card.category) domains.add(card.category);
  });
  return ['All', ...Array.from(domains).sort()];
});

// 3. Filter the cards based on what they click in the dropdown
const filteredLibraryFlashcards = computed(() => {
  if (librarySelectedDomain.value === 'All') {
    return libraryFlashcards.value;
  }
  return libraryFlashcards.value.filter(c => c.category === librarySelectedDomain.value);
});

// 🚨 THE NEW ENGINE STARTER
const bootQuizEngine = async (mode) => {
    // 1. Reset all previous state
    resetSession();
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
    else if (mode === 'flashcards') {
    currentView.value = 'flashcards';
    await fetchFlashcards();
    return;
    }

    // 4. Change the screen to the unified quiz UI
    currentView.value = 'quiz';

    // 5. Fetch the very first question!
    await fetchQuestion();
  };

// --- 🎴 DYNAMIC FLASHCARD ENGINE ---

const deleteAdminFlashcard = async (id) => {
  if (!confirm("⚠ VAPORIZE FLASHCARD? This cannot be undone.")) return;
  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/questions/flashcards/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) fetchAdminFlashcards();
  } catch (error) {
    console.error("Failed to vaporize:", error);
  }
};

watch(activeForgeTab, (newTab) => {
  if (newTab === 'study_cards' && selectedCert.value) {
    console.log("🃏 Tab clicked! Fetching flashcards for:", selectedCert.value.code);
    fetchLibraryFlashcards(selectedCert.value.code);
  }
});

// Also, let's make sure it fetches immediately if they load directly into the tab
onMounted(() => {
    // 1. Fetch Flashcards if needed
    if (activeForgeTab.value === 'study_cards' && selectedCert.value) {
      fetchLibraryFlashcards(selectedCert.value.code);
    }

    // 2. Fetch the Social Proof Reviews
    fetchReviews();

    // 3. Check founder spots
    fetchFounderSpots();
  });

// 🚨 THE SUBMISSION FUNCTION
const submitUserReview = async () => {
    if (!reviewForm.value.reviewText) {
      reviewFeedbackMessage.value = "Please enter your feedback.";
      return;
    }

    try {
      const token = localStorage.getItem('aws_jwt');
      const authorName = currentUser.value?.username || 'Anonymous Architect';

      const response = await fetch(`${API_BASE_URL}/api/questions/reviews`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          authorName: authorName,
          rating: reviewForm.value.rating,
          reviewText: reviewForm.value.reviewText,
          jobTitle: reviewForm.value.jobTitle
        })
      });

      if (response.ok) {
        reviewFeedbackMessage.value = "✅ Database updated. Thank you!";

        // Dramatic pause, then close the modal and update the homepage instantly!
        setTimeout(() => {
          showRatingModal.value = false;
          reviewForm.value.reviewText = '';
          reviewFeedbackMessage.value = '';
          fetchReviews(); // 🚨 This is the magic line that updates the homepage dynamically!
        }, 2000);
      } else {
        reviewFeedbackMessage.value = "❌ Authentication error. Are you logged in?";
      }
    } catch (error) {
      reviewFeedbackMessage.value = "❌ Critical Error: Could not reach the Matrix.";
    }
  };

// 1. Add the Certifications list
const certifications = ref([
    // --- FOUNDATIONAL ---
    {
      code: 'CLF-C02', name: 'Cloud Practitioner', category: 'Foundational',
      image: '/badges/clf-badge.png', color: 'bg-orange-400', icon: '☁️',
      domains: [
        { name: 'Domain 1: Cloud Concepts', description: 'Define the AWS Cloud, its value proposition, and basic cloud economics.', icon: '💡', modules: [] },
        { name: 'Domain 2: Security & Compliance', description: 'Shared responsibility model, access management, and security capabilities.', icon: '🛡️', modules: [] },
        { name: 'Domain 3: Cloud Technology & Services', description: 'Core AWS services spanning compute, network, database, and storage.', icon: '⚙️', modules: [] },
        { name: 'Domain 4: Billing & Pricing', description: 'Pricing models, account structures, and billing support.', icon: '💳', modules: [] }
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
        { name: 'Domain 1: Development with AWS Services', description: 'Lambda, API Gateway, DynamoDB, S3, SQS/SNS, Step Functions.', icon: '👨‍💻', modules: [] },
        { name: 'Domain 2: Security', description: 'IAM, Cognito, encryption, secrets management, application security.', icon: '🔑', modules: [] },
        { name: 'Domain 3: Deployment', description: 'CI/CD, Elastic Beanstalk, CloudFormation, SAM, containers.', icon: '🚢', modules: [] },
        { name: 'Domain 4: Troubleshooting & Optimization', description: 'CloudWatch, X-Ray, performance tuning, caching, cost optimization.', icon: '🔧', modules: [] }
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
        { name: 'Domain 1: Data Ingestion & Transformation', description: 'Design data pipelines, ETL processes, and stream processing.', icon: '🌊', modules: [] },
        { name: 'Domain 2: Data Store Management', description: 'Manage operational databases, data warehouses, and data lakes.', icon: '💾', modules: [] },
        { name: 'Domain 3: Data Operations & Support', description: 'Automate data processing and maintain pipeline reliability.', icon: '⚙️', modules: [] },
        { name: 'Domain 4: Data Security & Governance', description: 'Implement data encryption, auditing, and access controls.', icon: '🔒', modules: [] }
      ]
    },

    // --- PROFESSIONAL ---
    {
      code: 'AIP-C01', name: 'Generative AI Developer', category: 'Professional',
      image: '/badges/aip-badge.png', color: 'bg-cyan-500', icon: '🧠',
      domains: [
        { name: 'Domain 1: GenAI Fundamentals', description: 'FM integration, data management, vector stores, RAG, and compliance.', icon: '📚', modules: [] },
        { name: 'Domain 2: Implementation & Integration', description: 'Agentic AI, deployment strategies, enterprise integration, and resilience.', icon: '🏭', modules: [] },
        { name: 'Domain 3: AI Safety & Governance', description: 'Security, responsible AI, guardrails, and compliance frameworks.', icon: '🛡️', modules: [] },
        { name: 'Domain 4: Operational Efficiency', description: 'Cost optimization, monitoring, scaling, and performance tuning.', icon: '⚡', modules: [] },
        { name: 'Domain 5: Testing & Troubleshooting', description: 'Evaluation metrics, debugging, validation, and model comparison.', icon: '🔍', modules: [] }
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
        { name: 'Domain 1: SDLC Automation', description: 'Implement CI/CD pipelines, deployment strategies, and automated testing.', icon: '⚙️', modules: [] },
        { name: 'Domain 2: Configuration & IaC', description: 'Manage infrastructure as code with CloudFormation, CDK, and configuration management.', icon: '📜', modules: [] },
        { name: 'Domain 3: Resilient Architectures', description: 'Design highly available, fault-tolerant, and self-healing systems.', icon: '🏗️', modules: [] },
        { name: 'Domain 4: Monitoring & Logging', description: 'Implement observability, centralized logging, and automated alerting.', icon: '📊', modules: [] },
        { name: 'Domain 5: Incident & Event Response', description: 'Automate incident detection, routing, and remediation.', icon: '🚨', modules: [] },
        { name: 'Domain 6: Security & Compliance', description: 'Implement security automation, secrets management, and compliance as code.', icon: '🔒', modules: [] }
      ]
    },

    // --- SPECIALTY ---
    {
      code: 'MLS-C01', name: 'Machine Learning', category: 'Specialty',
      image: '/badges/mls-badge.png', color: 'bg-purple-600', icon: '🤖',
      domains: [
        {
          name: 'Domain 1: Data Engineering', description: 'Data ingestion, transformation, and feature engineering for ML.', icon: '🗄️',
          modules: [
            { id: 'mls1-1', title: 'Building S3 Data Lakes', duration: '14:00', type: 'video', videoUrl: 'https://www.youtube.com/embed/k1RI5locZE4?autoplay=1' },
            { id: 'mls1-2', title: 'ETL Pipelines with AWS Glue', duration: '20:45', type: 'video', videoUrl: 'https://www.youtube.com/embed/5T2k2n0P3q8?autoplay=1' },
            { id: 'mls1-3', title: 'Amazon Kinesis Data Streams', duration: '18:10', type: 'video', videoUrl: 'https://www.youtube.com/embed/1c18L3kK-0o?autoplay=1' }
          ]
        },
        {
          name: 'Domain 2: Exploratory Data Analysis', description: 'Sanitizing, preparing, and visualizing data for modeling.', icon: '📊',
          modules: [
            { id: 'mls2-1', title: 'Handling Missing Data & Outliers', duration: '12:30', type: 'video', videoUrl: 'https://www.youtube.com/embed/8b512c-n-X8?autoplay=1' },
            { id: 'mls2-2', title: 'Data Visualization with QuickSight', duration: '15:20', type: 'video', videoUrl: 'https://www.youtube.com/embed/K81l-R1Xl7A?autoplay=1' },
            { id: 'mls2-3', title: 'Feature Engineering Techniques', duration: 'Read', type: 'document' }
          ]
        },
        {
          name: 'Domain 3: Modeling', description: 'Framing problems, selecting algorithms, and training ML models.', icon: '🧠',
          modules: [
            { id: 'mls3-1', title: 'Built-in SageMaker Algorithms', duration: '25:00', type: 'video', videoUrl: 'https://www.youtube.com/embed/YpQ_M-K535U?autoplay=1' },
            { id: 'mls3-2', title: 'Hyperparameter Tuning Strategies', duration: '16:40', type: 'video', videoUrl: 'https://www.youtube.com/embed/u1Q_q0XfJ2M?autoplay=1' },
            { id: 'mls3-3', title: 'Evaluating Model Performance Metrics', duration: '19:15', type: 'video', videoUrl: 'https://www.youtube.com/embed/p1I2bI2d9Z4?autoplay=1' }
          ]
        },
        {
          name: 'Domain 4: ML Implementation & Ops', description: 'Deploying, monitoring, and maintaining models in production.', icon: '🚀',
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
        { name: 'Domain 1: Network Design', description: 'VPC architecture, CIDR planning, hybrid connectivity, multi-region topologies, and edge networking.', icon: '📐', modules: [] },
        { name: 'Domain 2: Network Implementation', description: 'Implement routing, Direct Connect, Transit Gateway, and VPN solutions.', icon: '🔌', modules: [] },
        { name: 'Domain 3: Network Management', description: 'Operate, maintain, and monitor complex AWS network infrastructure.', icon: '🎛️', modules: [] },
        { name: 'Domain 4: Network Security', description: 'Implement network security controls, firewalls, and encryption in transit.', icon: '🔒', modules: [] }
      ]
    }
  ]);

// 📚 The Central Data Dictionary for All Certifications
const allCertDomains = {
  'CLF-C02': [
    {
      title: "Domain 1: Cloud Concepts", weight: "24%",
      topics: ["Six Advantages of Cloud", "Cloud Deployment Models (Public/Hybrid/Private)", "Well-Architected Framework (6 Pillars)", "Shared Responsibility Model", "Cloud Economics (CapEx vs OpEx)"],
      keyFact: "The Shared Responsibility Model is the most tested concept: AWS secures the cloud infrastructure; you secure everything you put in it."
    },
    {
      title: "Domain 2: Security & Compliance", weight: "30%",
      topics: ["IAM (Users, Groups, Roles, Policies)", "MFA & Access Keys", "AWS Organizations & SCPs", "Security Services (GuardDuty, Inspector, Macie)", "Compliance Programs"],
      keyFact: "IAM policies follow least privilege: grant only the minimum permissions needed to perform a task."
    },
    {
      title: "Domain 3: Cloud Technology & Services", weight: "34%",
      topics: ["EC2, Lambda, ECS", "S3, EBS, EFS", "VPC, CloudFront, Route 53", "RDS, DynamoDB, ElastiCache", "CloudFormation, CloudWatch, Trusted Advisor"],
      keyFact: "This is the largest domain — know what each core service does and when to use it, not how to configure it."
    },
    {
      title: "Domain 4: Billing & Pricing", weight: "12%",
      topics: ["Pricing Models (On-Demand, Reserved, Spot, Savings Plans)", "AWS Free Tier", "AWS Budgets & Cost Explorer", "Support Plans", "AWS Organizations (consolidated billing)"],
      keyFact: "Savings Plans and Reserved Instances offer up to 72% savings for steady-state workloads; Spot Instances save up to 90% for interruptible workloads."
    }
  ],
  'SAA-C03': [
    {
      title: "Domain 1: Design Secure Architectures", weight: "30%",
      topics: ["IAM (Roles, Policies, Federation)", "VPC (Subnets, NACLs, Security Groups)", "Encryption (KMS, ACM, S3)", "AWS Organizations & SCPs", "WAF, Shield, GuardDuty"],
      keyFact: "Security Groups are stateful (return traffic auto-allowed). NACLs are stateless (must explicitly allow both inbound and outbound)."
    },
    {
      title: "Domain 2: Design Resilient Architectures", weight: "26%",
      topics: ["Multi-AZ & Multi-Region", "Auto Scaling & ELB", "Decoupling (SQS, SNS, EventBridge)", "Backup & DR Strategies", "Route 53 Failover"],
      keyFact: "RTO = how fast you recover. RPO = how much data you can lose. Pilot Light < Warm Standby < Active-Active in cost and recovery speed."
    },
    {
      title: "Domain 3: Design High-Performing Architectures", weight: "24%",
      topics: ["EC2 Instance Types & Placement Groups", "EBS vs. EFS vs. S3", "CloudFront & Global Accelerator", "RDS vs. DynamoDB vs. ElastiCache", "Lambda & Serverless Patterns"],
      keyFact: "CloudFront caches at edge locations for static content. Global Accelerator uses AWS backbone for dynamic content with static IPs."
    },
    {
      title: "Domain 4: Design Cost-Optimized Architectures", weight: "20%",
      topics: ["EC2 Pricing (On-Demand, Reserved, Spot, Savings Plans)", "S3 Storage Classes & Lifecycle", "Right-sizing & Compute Optimizer", "Data Transfer Costs", "Serverless vs. Provisioned"],
      keyFact: "S3 Intelligent-Tiering automatically moves objects between access tiers with no retrieval fees - ideal when access patterns are unknown."
    }
  ],
  'DVA-C02': [
    {
      title: "Domain 1: Development with AWS Services", weight: "32%",
      topics: ["Lambda (invocation, concurrency, layers, aliases)", "API Gateway (REST/HTTP, proxy, stages, caching)", "DynamoDB (keys, RCU/WCU, GSI, Streams, TTL)", "S3 (pre-signed URLs, multipart, events)", "SQS/SNS (Standard/FIFO, DLQ, fan-out)"],
      keyFact: "Lambda initialization code outside the handler runs once per cold start and is reused across warm invocations. Always put SDK clients and DB connections outside the handler."
    },
    {
      title: "Domain 2: Security", weight: "26%",
      topics: ["IAM (roles, policies, STS, cross-account)", "Cognito (User Pools, Identity Pools, triggers)", "KMS (envelope encryption, encryption context)", "Secrets Manager vs. Parameter Store", "API Gateway Authorization (IAM, Cognito, Lambda)"],
      keyFact: "Cognito User Pools handle authentication (JWT tokens). Identity Pools handle authorization (temporary AWS credentials). They're often used together."
    },
    {
      title: "Domain 3: Deployment", weight: "24%",
      topics: ["Elastic Beanstalk (deployment policies, .ebextensions)", "CloudFormation (SAM, nested stacks, transforms)", "CodeDeploy (EC2/ECS/Lambda strategies)", "CodePipeline + CodeBuild", "Container Deployment (ECS, ECR, Fargate)"],
      keyFact: "SAM (Serverless Application Model) is a CloudFormation extension that simplifies serverless deployments with shorthand syntax for Lambda, API Gateway, and DynamoDB."
    },
    {
      title: "Domain 4: Troubleshooting & Optimization", weight: "18%",
      topics: ["CloudWatch (metrics, logs, alarms, EMF, Agent)", "X-Ray (traces, annotations, sampling, service map)", "Lambda errors (timeout, OOM, throttling, cold starts)", "DynamoDB optimization (DAX, key design, capacity)", "Caching (CloudFront, API GW, ElastiCache, DAX)"],
      keyFact: "X-Ray Annotations are indexed and searchable (filter traces by user_id). Metadata is NOT indexed. Use annotations for fields you'll filter on."
    }
  ],
  'SOA-C02': [
    {
      title: "Domain 1: Monitoring, Logging & Remediation", weight: "20%",
      topics: ["CloudWatch (Metrics, Logs, Alarms, Dashboards)", "EventBridge (Rules, Targets, Automation)", "CloudTrail (API Auditing)", "AWS Config (Compliance Rules)", "Auto-Remediation (SSM, Lambda)"],
      keyFact: "CloudWatch Composite Alarms combine multiple alarms with AND/OR logic to reduce alarm noise and trigger only on correlated failures."
    },
    {
      title: "Domain 2: Reliability & Business Continuity", weight: "15%",
      topics: ["Multi-AZ & Multi-Region", "Auto Scaling (Policies, Lifecycle Hooks)", "Backup & Restore (AWS Backup)", "DR Strategies (Pilot Light, Warm Standby)", "Route 53 Health Checks & Failover"],
      keyFact: "AWS Backup provides centralized, policy-based backup across services (EC2, RDS, EFS, DynamoDB, S3) with cross-region and cross-account copy."
    },
    {
      title: "Domain 3: Deployment, Provisioning & Automation", weight: "18%",
      topics: ["CloudFormation (Stacks, StackSets, Drift Detection)", "Systems Manager (Run Command, Patch Manager, State Manager)", "Elastic Beanstalk (Deployment Policies)", "AMI Management & EC2 Image Builder", "OpsWorks & Automation Documents"],
      keyFact: "CloudFormation StackSets deploy infrastructure across multiple accounts and regions from a single template. Drift detection identifies manual changes."
    },
    {
      title: "Domain 4: Security & Compliance", weight: "16%",
      topics: ["IAM (Policies, Roles, Permission Boundaries)", "AWS Config Rules & Conformance Packs", "GuardDuty, Inspector, Macie", "KMS & Encryption", "AWS Organizations & SCPs"],
      keyFact: "AWS Config continuously evaluates resource configurations against rules. Auto-remediation via SSM Automation documents can fix non-compliant resources automatically."
    },
    {
      title: "Domain 5: Networking & Content Delivery", weight: "18%",
      topics: ["VPC (Subnets, Route Tables, NAT, Peering)", "VPN & Direct Connect", "CloudFront & Route 53", "VPC Endpoints (Gateway vs Interface)", "Troubleshooting Connectivity"],
      keyFact: "VPC Flow Logs capture metadata (not content) at ENI/subnet/VPC level. Use Reachability Analyzer for path analysis without sending traffic."
    },
    {
      title: "Domain 6: Cost & Performance Optimization", weight: "13%",
      topics: ["Cost Explorer & Budgets", "Trusted Advisor & Compute Optimizer", "S3 Storage Classes & Lifecycle", "EC2 Pricing (Reserved, Spot, Savings Plans)", "Performance Monitoring & Right-Sizing"],
      keyFact: "AWS Compute Optimizer uses ML to analyze utilization and recommend optimal EC2 instance types, EBS volumes, and Lambda memory settings."
    }
  ],
  'AIF-C01': [
    {
      title: "Domain 1: Fundamentals of AI & ML", weight: "20%",
      topics: ["Supervised vs. Unsupervised Learning", "Deep Learning vs. Traditional ML", "Key Terminology (Epochs, Weights, Bias)"],
      keyFact: "Supervised learning requires labeled data; Unsupervised looks for hidden patterns in unlabeled data."
    },
    {
      title: "Domain 2: Fundamentals of Generative AI", weight: "24%",
      topics: ["Large Language Models (LLMs)", "Foundation Models", "Tokenization", "Prompt Engineering"],
      keyFact: "Temperature controls randomness in Generative AI. High temp = creative, Low temp = deterministic."
    },
    {
      title: "Domain 3: Applications of Foundation Models", weight: "28%",
      topics: ["Amazon Bedrock", "Amazon Q", "Amazon SageMaker JumpStart"],
      keyFact: "Amazon Bedrock is a fully managed service that offers a choice of high-performing foundation models via a single API."
    },
    {
      title: "Domain 4: Responsible AI & Security", weight: "28%",
      topics: ["Data Privacy in AWS", "Mitigating Bias", "Amazon Bedrock Guardrails"],
      keyFact: "Bedrock Guardrails allow you to implement safeguards across foundation models based on your specific use cases."
    }
  ],
  'AIP-C01': [
    {
      title: "Domain 1: GenAI Fundamentals", weight: "31%",
      topics: ["FM Selection & Configuration", "RAG Architecture", "Vector Stores & Retrieval", "Chunking Strategies", "Data Compliance"],
      keyFact: "Bedrock Knowledge Bases handles chunking, embedding, indexing, and retrieval as a managed RAG solution."
    },
    {
      title: "Domain 2: Implementation & Integration", weight: "26%",
      topics: ["Bedrock Agents & Action Groups", "Multi-Agent Collaboration", "Step Functions Orchestration", "API Patterns (Sync/Stream/Async)", "Error Handling & Resilience"],
      keyFact: "Agents use the ReAct pattern (Reasoning + Acting) to autonomously plan, execute tools, and adapt based on observations."
    },
    {
      title: "Domain 3: AI Safety & Governance", weight: "20%",
      topics: ["Bedrock Guardrails", "Prompt Injection Mitigation", "Shared Responsibility Model", "IAM for AI", "Compliance Frameworks"],
      keyFact: "Bedrock guarantees your data is never used to train base models and is never shared with model providers."
    },
    {
      title: "Domain 4: Operational Efficiency", weight: "12%",
      topics: ["Cost Optimization", "Provisioned Throughput", "Monitoring & Observability", "Scaling Strategies", "Caching"],
      keyFact: "Use smaller models for simple tasks and Provisioned Throughput for steady high-volume workloads to optimize cost."
    },
    {
      title: "Domain 5: Testing & Troubleshooting", weight: "11%",
      topics: ["Model Evaluation Metrics", "ROUGE & BLEU Scores", "Bedrock Evaluations", "Debugging RAG", "A/B Testing Models"],
      keyFact: "Bedrock Evaluations lets you run automatic metrics, human evaluation, and custom criteria against your test dataset."
    }
  ],
  'SAP-C02': [
    {
      title: "Domain 1: Design for Organizational Complexity", weight: "26%",
      topics: ["Multi-Account Strategy (Organizations, Control Tower)", "Cross-Account Access (RAM, STS, Resource Policies)", "Complex Networking (Transit Gateway, Direct Connect, PrivateLink)", "Hybrid Architectures", "Identity Federation (SSO, SAML, OIDC)"],
      keyFact: "AWS Control Tower automates multi-account setup with guardrails (preventive SCPs + detective Config rules) and an Account Factory for standardized provisioning."
    },
    {
      title: "Domain 2: Design for New Solutions", weight: "29%",
      topics: ["Serverless at Scale (Lambda, Step Functions, EventBridge)", "Container Orchestration (ECS, EKS, Fargate)", "Data Lakes & Analytics (Lake Formation, Athena, Redshift)", "ML Integration (SageMaker, Bedrock)", "IoT & Edge Computing"],
      keyFact: "For massive scale, design with event-driven architectures: EventBridge for routing, SQS for buffering, Lambda for processing, and DynamoDB for state."
    },
    {
      title: "Domain 3: Continuous Improvement for Existing Solutions", weight: "25%",
      topics: ["Performance Optimization (Caching, CDN, DB Tuning)", "Cost Optimization (Savings Plans, Spot, Right-Sizing)", "Operational Excellence (Automation, IaC, Observability)", "Reliability Improvements (Multi-Region, Chaos Engineering)", "Security Hardening"],
      keyFact: "Well-Architected Reviews identify improvement areas across all 6 pillars. Use AWS Well-Architected Tool for structured assessments."
    },
    {
      title: "Domain 4: Accelerate Workload Migration", weight: "20%",
      topics: ["Migration Strategies (7 Rs)", "AWS Migration Hub & MGN", "Database Migration (DMS, SCT)", "Large-Scale Data Transfer (Snow, DataSync)", "Modernization (Containers, Serverless, Microservices)"],
      keyFact: "The 7 Rs: Retire, Retain, Rehost (lift-and-shift), Relocate, Repurchase, Replatform (lift-tinker-shift), Refactor (re-architect). Each has different effort and value."
    }
  ],
  'DOP-C02': [
    {
      title: "Domain 1: SDLC Automation", weight: "22%",
      topics: ["CodePipeline (V2, triggers, execution modes)", "CodeBuild (buildspec, caching, VPC)", "CodeDeploy (EC2/ECS/Lambda strategies)", "Deployment Strategies (Blue/Green, Canary, Linear)", "Automated Testing & Quality Gates"],
      keyFact: "CodeDeploy deployment types vary by compute: EC2 supports In-Place and Blue/Green; ECS supports Blue/Green only; Lambda supports Canary, Linear, and AllAtOnce."
    },
    {
      title: "Domain 2: Configuration & IaC", weight: "17%",
      topics: ["CloudFormation (stacks, nested, StackSets, drift)", "AWS CDK (constructs, pipelines)", "Systems Manager (SSM, Parameter Store, Automation)", "AWS Config (rules, remediation)", "AMI Management & Patching"],
      keyFact: "CloudFormation StackSets deploy infrastructure across multiple accounts and regions simultaneously from a single template."
    },
    {
      title: "Domain 3: Resilient Architectures", weight: "15%",
      topics: ["Multi-AZ & Multi-Region", "Auto Scaling (predictive, scheduled, dynamic)", "Self-Healing (health checks, replacement)", "DR Strategies (Pilot Light, Warm Standby, Active-Active)", "Chaos Engineering"],
      keyFact: "Auto Scaling lifecycle hooks let you perform custom actions (drain connections, deregister) before instances terminate."
    },
    {
      title: "Domain 4: Monitoring & Logging", weight: "15%",
      topics: ["CloudWatch (metrics, logs, alarms, dashboards)", "X-Ray (distributed tracing)", "Centralized Logging (Kinesis, OpenSearch)", "CloudWatch Logs Insights", "Composite Alarms & Anomaly Detection"],
      keyFact: "CloudWatch Logs Insights enables interactive search and analysis of log data using a purpose-built query language."
    },
    {
      title: "Domain 5: Incident & Event Response", weight: "18%",
      topics: ["EventBridge (rules, event-driven automation)", "Systems Manager Incident Manager", "Auto-Remediation (Lambda, SSM Automation)", "Runbooks & Playbooks", "ChatOps (AWS Chatbot)"],
      keyFact: "EventBridge + Lambda is the standard pattern for automated incident response: detect event, trigger remediation, notify team."
    },
    {
      title: "Domain 6: Security & Compliance", weight: "13%",
      topics: ["Secrets Manager & Parameter Store", "IAM (roles, policies, cross-account)", "AWS Config (compliance rules)", "Security Automation (GuardDuty, Inspector)", "Compliance as Code (SCPs, Config)"],
      keyFact: "AWS Config rules provide continuous compliance monitoring; auto-remediation via SSM Automation documents can fix non-compliant resources automatically."
    }
  ],
  'ANS-C01': [
    {
      title: "Domain 1: Network Design", weight: "30%",
      topics: ["VPC Architecture & CIDR Planning", "Transit Gateway & Multi-Account", "Direct Connect & VPN", "BGP Routing", "Route 53 & DNS"],
      keyFact: "Transit Gateway is the default hub for multi-VPC connectivity at scale, supporting route table segmentation to isolate environments."
    },
    {
      title: "Domain 2: Network Implementation", weight: "26%",
      topics: ["Direct Connect Setup", "Transit Gateway Configuration", "VPN Tunnels & ECMP", "Route Table Management", "PrivateLink Deployment"],
      keyFact: "Transit VIF connects DX to TGW (access many VPCs); Private VIF connects DX to a single VGW (access one VPC)."
    },
    {
      title: "Domain 3: Network Management", weight: "20%",
      topics: ["CloudWatch Network Monitoring", "VPC Flow Logs", "Traffic Mirroring", "Network Manager", "Reachability Analyzer"],
      keyFact: "VPC Flow Logs capture IP traffic metadata (not packet content) at the ENI, subnet, or VPC level for troubleshooting and compliance."
    },
    {
      title: "Domain 4: Network Security", weight: "24%",
      topics: ["AWS Network Firewall", "Security Groups & NACLs", "WAF & Shield", "TLS/Encryption in Transit", "DDoS Mitigation"],
      keyFact: "AWS Network Firewall provides stateful inspection with Suricata-compatible rules, deployed in dedicated subnets with TGW routing for centralized inspection."
    }
  ],
  'DEA-C01': [
    {
      title: "Domain 1: Data Ingestion & Transformation", weight: "34%",
      topics: ["Kinesis (Streams, Firehose, Analytics)", "AWS Glue (ETL, Catalog, Crawlers, Bookmarks)", "Amazon MSK", "DMS & CDC", "Data Formats (Parquet, ORC, Avro)"],
      keyFact: "Glue is the #1 service in this domain. Know Data Catalog, Crawlers, ETL Jobs, Bookmarks, DynamicFrames, and Data Quality."
    },
    {
      title: "Domain 2: Data Store Management", weight: "26%",
      topics: ["S3 Data Lake Architecture", "Redshift (Distribution, Sort Keys)", "DynamoDB Design", "Lake Formation", "Data Lifecycle Management"],
      keyFact: "Redshift distribution styles (KEY, EVEN, ALL) and sort keys are critical for query performance optimization."
    },
    {
      title: "Domain 3: Data Operations & Support", weight: "22%",
      topics: ["Pipeline Monitoring (CloudWatch)", "Data Quality Validation", "Troubleshooting ETL Failures", "Auto-scaling & Performance", "Cost Optimization"],
      keyFact: "Glue job bookmarks track processed data to enable incremental ETL without reprocessing."
    },
    {
      title: "Domain 4: Data Security & Governance", weight: "18%",
      topics: ["Lake Formation Permissions", "Encryption (KMS, S3, Redshift)", "VPC & Network Security", "Data Classification (Macie)", "Compliance & Auditing"],
      keyFact: "Lake Formation provides fine-grained column-level and row-level security on data lake resources."
    }
  ],
  'MLS-C01': [
    {
      title: "Domain 1: Data Engineering", weight: "20%",
      topics: ["S3 Data Lakes", "Kinesis (Streams, Firehose, Analytics)", "AWS Glue (ETL, Catalog, Crawlers)", "Data Formats (RecordIO, Parquet, CSV)", "Data Pipeline Orchestration"],
      keyFact: "RecordIO (protobuf) is the fastest format for SageMaker Pipe mode training. Parquet is best for analytics. CSV is universal but slow."
    },
    {
      title: "Domain 2: Exploratory Data Analysis", weight: "24%",
      topics: ["Statistics & Distributions", "Feature Engineering (Scaling, Encoding)", "Missing Data & Outliers", "Dimensionality Reduction (PCA, t-SNE)", "Imbalanced Data (SMOTE)"],
      keyFact: "PCA requires standardization (Z-score) before applying. t-SNE is for visualization ONLY. SMOTE only on training data."
    },
    {
      title: "Domain 3: Modeling", weight: "36%",
      topics: ["SageMaker Built-in Algorithms", "Hyperparameter Tuning", "Model Evaluation Metrics", "Regularization (L1/L2)", "Deep Learning Frameworks"],
      keyFact: "XGBoost is the most tested algorithm. Know: objective, num_round, max_depth, eta, subsample, colsample_bytree, scale_pos_weight."
    },
    {
      title: "Domain 4: ML Implementation & Operations", weight: "20%",
      topics: ["SageMaker Endpoints (Real-time, Batch, Async)", "Model Monitor & Drift Detection", "A/B Testing & Multi-Model", "MLOps Pipelines", "Security & IAM for ML"],
      keyFact: "Model Monitor detects data drift, model quality drift, bias drift, and feature attribution drift in production endpoints."
    }
  ],
  'SCS-C02': [
    {
      title: "Domain 1: Threat Detection & Incident Response", weight: "14%",
      topics: ["GuardDuty (Threat Detection, Finding Types)", "Security Hub (Aggregation, Compliance)", "Detective (Investigation)", "Incident Response Automation", "Forensics & Containment"],
      keyFact: "GuardDuty uses ML to analyze VPC Flow Logs, DNS logs, CloudTrail, S3 data events, and EKS audit logs to detect threats like compromised instances and credential exfiltration."
    },
    {
      title: "Domain 2: Security Logging & Monitoring", weight: "18%",
      topics: ["CloudTrail (Management/Data Events, Insights)", "VPC Flow Logs", "CloudWatch (Logs, Metrics, Alarms)", "S3 Access Logs", "Centralized Logging Architecture"],
      keyFact: "CloudTrail Organization Trail captures API calls across ALL accounts. Log file integrity validation detects tampering using SHA-256 hash chains."
    },
    {
      title: "Domain 3: Infrastructure Security", weight: "20%",
      topics: ["VPC Security (NACLs, Security Groups, Endpoints)", "AWS Network Firewall (Stateful/Stateless)", "WAF & Shield Advanced", "DDoS Mitigation", "Systems Manager & Patch Management"],
      keyFact: "AWS Network Firewall provides stateful deep packet inspection with Suricata-compatible IPS rules, deployed in dedicated subnets with centralized TGW routing."
    },
    {
      title: "Domain 4: Identity & Access Management", weight: "16%",
      topics: ["Advanced IAM Policies (Conditions, Variables)", "Cross-Account Access (STS, Resource Policies)", "Federation (SAML, OIDC, Identity Center)", "Permission Boundaries & SCPs", "Cognito & API Authorization"],
      keyFact: "IAM policy conditions enable fine-grained control: aws:SourceIp, aws:RequestedRegion, aws:PrincipalOrgID, kms:ViaService, s3:prefix for per-user access."
    },
    {
      title: "Domain 5: Data Protection", weight: "32%",
      topics: ["KMS (Key Policies, Grants, Envelope Encryption)", "S3 Encryption (SSE-S3/KMS/C, Client-side)", "Certificate Management (ACM, Private CA)", "Secrets Management (Secrets Manager, Parameter Store)", "Data Classification (Macie)"],
      keyFact: "KMS key policies are the PRIMARY access control for CMKs. Without key policy access, even IAM admin policies cannot use the key. Always check key policy first."
    }
  ]
};

// 🚨 THE ROUTER: This automatically swaps the data when the cert changes!
const currentForgeDomains = computed(() => {
  if (!selectedCert.value) return [];
  // If we don't have data for the specific cert yet, fallback to an empty array or a default
  return allCertDomains[selectedCert.value.code] || allCertDomains['AIF-C01'];
  });

// 1. Catches the file when the user picks it
const handleFileSelect = (event) => {
    selectedCsvFile.value = event.target.files[0];
    uploadStatus.value = ''; // Reset the status message
  };

// 2. Ships the file to your Java backend (NOW SECURED WITH JWT)
const uploadCsv = async () => {
    if (!selectedCsvFile.value) {
      uploadStatus.value = 'Please select a file first.';
      return;
    }

    const formData = new FormData();
    formData.append('file', selectedCsvFile.value);

    uploadStatus.value = 'Injecting into mainframes...';

    try {
      // 🚨 CRITICAL FIX: Grab the user's security token from the browser!
      const token = localStorage.getItem('aws_jwt');

      const response = await fetch(`${API_BASE_URL}/api/questions/upload`, {
        method: 'POST',
        headers: {
          // 🚨 Include the token so Spring Boot doesn't block the upload!
          'Authorization': `Bearer ${token}`,
          'X-Admin-Key': 'masterkey'
        },
        body: formData,
      });

      if (response.ok) {
        const result = await response.json();
        uploadStatus.value = result.message;
        selectedCsvFile.value = null;

        // Refresh the admin table instantly
        if (typeof fetchAdminQuestions === 'function') {
          fetchAdminQuestions();
        }
      } else {
        uploadStatus.value = '❌ Upload failed. The CSV format might be invalid or server rejected it.';
      }
    } catch (error) {
      console.error('Upload error:', error);
      uploadStatus.value = '❌ Server connection lost.';
    }
  };

const handleFlashcardSelect = (event) => {
  selectedFlashcardCsv.value = event.target.files[0];
  flashcardUploadStatus.value = '';
};

const uploadFlashcardCsv = async () => {
  if (!selectedFlashcardCsv.value) return;
  const formData = new FormData();
  formData.append('file', selectedFlashcardCsv.value);
  flashcardUploadStatus.value = 'Injecting flashcards into matrix...';

  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/questions/flashcards/upload`, {
      method: 'POST',
      headers: {'X-Admin-Key': 'masterkey', 'Authorization': `Bearer ${token}` },
      body: formData,
    });

    if (response.ok) {
      const result = await response.json();
      flashcardUploadStatus.value = `✅ ${result.message}`;
      selectedFlashcardCsv.value = null;
      if (typeof fetchAdminFlashcards === 'function') fetchAdminFlashcards();
    } else {
      // 🚨 THE UNMASKER: If Java rejects the file, catch the raw text so we can read it!
      const errorText = await response.text();
      console.error("RAW JAVA ERROR:", errorText);
      flashcardUploadStatus.value = `❌ Upload Failed. Press F12 and check the Console.`;
    }
  } catch (error) {
    console.error("Network Crash:", error);
    flashcardUploadStatus.value = '❌ Server connection lost.';
  }
};

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
    const response = await fetch(`${API_BASE_URL}/api/questions/leaderboard`);
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
    const response = await fetch(`${API_BASE_URL}/api/auth${endpoint}`, {
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

    // Determine max allowed selections based on question text ONLY
    let maxAllowed = 1;

    if (isMultipleAnswer(question.value)) {
      const text = question.value.questionText;
      if (text.includes("THREE") || text.includes("Choose 3") || text.includes("Select 3") || text.includes("select three")) {
        maxAllowed = 3;
      } else {
        maxAllowed = 2; // Default for AWS multi-select is 2
      }
    }

    // Selection Logic
    if (maxAllowed === 1) {
      // Single choice: just replace the array
      selectedAnswers.value = [optionId];
    } else {
      // Multi-choice: Toggle logic
      const index = selectedAnswers.value.indexOf(optionId);
      if (index > -1) {
        selectedAnswers.value.splice(index, 1);
      } else if (selectedAnswers.value.length < maxAllowed) {
        selectedAnswers.value.push(optionId);
      }
    }
  };

const deleteAttempt = async (attemptId) => {
  try {
    const token = localStorage.getItem('aws_jwt');
    const response = await fetch(`${API_BASE_URL}/api/questions/history/${attemptId}`, {
      method: 'DELETE',
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    });

    if (response.ok) {
      showToast("Attempt purged from archives.", "success");
      examHistory.value = examHistory.value.filter(a => String(a.id) !== String(attemptId));
    } else {
      showToast("Failed to delete. Server rejected the request.", "error");
    }
  } catch (error) {
    showToast("Connection failed. Could not delete.", "error");
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

// 1. Controls the visibility of the sleek Abort Modal
const showAbortModal = ref(false);

// 2. Triggered by your "Exit" button in the HTML
const abortQuiz = () => {
    // Instead of a browser alert, we just turn on our custom modal
    showAbortModal.value = true;
  };

// 3. Triggered ONLY if they click "Yes, Abort" inside the custom modal
const confirmAbort = () => {
    // 1. Kill the clocks
    stopTimer();
    if (amrapInterval) clearInterval(amrapInterval);

    // 2. Erase the auto-save memory so they don't resume it on refresh
    localStorage.removeItem('aws_active_exam');

    // 3. Wipe the board and send them to the lobby
    resetSession();

    // 4. Hide the modal
    showAbortModal.value = false;
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
  // 🚨 FIX 1: Added the VIP Pass to the Streak tracker
const recordStudySession = async () => {
    if (!currentUser.value) return;

    try {
      const token = localStorage.getItem('aws_jwt'); // Grab the token!

      const response = await fetch(`${API_BASE_URL}/api/users/${currentUser.value.username}/record-study`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // 🚨 THIS WAS MISSING
        }
      });

      if (response.ok) {
        const data = await response.json();
        userStreak.value = data.currentStreak;
      }
    } catch (error) {
      console.error("Failed to record study session:", error);
    }
  };

// 2. Fires ONLY after they check the box and agree
const proceedToStripe = async () => {
    if (!hasAcceptedTerms.value) return; // Failsafe: Don't run if they didn't agree

    const token = localStorage.getItem('aws_jwt');
    showAgreementModal.value = false; // Hide the contract

    // Prevent double-clicks!
    isCheckoutLoading.value = true;

    try {
      const response = await fetch(`${API_BASE_URL}/api/payment/checkout`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ planType: pendingPlanType.value, useFounderDiscount: founderSpotsActive.value && pendingPlanType.value !== 'trial' })
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('aws_awaiting_stripe_return', 'true');
        localStorage.setItem('aws_return_view', currentView.value);
        if (selectedCert.value) {
          localStorage.setItem('aws_return_cert', JSON.stringify(selectedCert.value));
        }
        window.location.href = data.checkoutUrl; // Warp to Stripe!

      } else if (response.status === 400) {
        // Catch double-charges
        const errorData = await response.json();
        if (errorData.error === "User is already a Premium member.") {
          showAlreadyPremiumModal.value = true;
        } else if (errorData.error && errorData.error.includes("Trial already used")) {
          showToast("You've already used your trial. Please choose Monthly or Annual.", "warning");
          pendingPlanType.value = 'monthly';
        } else {
          showToast(errorData.error || "Payment error.", "error");
        }
      } else {
        const errorData = await response.json();
        showToast(errorData.error || "Payment gateway error.", "error");
      }
    } catch (error) {
      console.error("Payment pipeline severed:", error);
      showToast("Secure connection failed. Check your network.", "error");
    } finally {
      isCheckoutLoading.value = false; // Unlock the UI
    }
  };

// 🚨 FIX 2: Added the VIP Pass to the XP tracker
const awardExperiencePoints = async () => {
    if (!currentUser.value) return;

    const correctCount = Math.round((finalScore.value / 100) * totalExamQuestions.value);

    try {
      const token = localStorage.getItem('aws_jwt'); // Grab the token!

      const response = await fetch(`${API_BASE_URL}/api/users/${currentUser.value.username}/award-xp`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // 🚨 THIS WAS MISSING
        },
        body: JSON.stringify({
          correctCount: correctCount,
          totalQuestions: totalExamQuestions.value
        })
      });

      if (response.ok) {
        const data = await response.json();
        userTotalXp.value = data.totalXp;
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
  // 1. Grab every answer the user ACTUALLY clicked "Submit" on
  const results = Object.values(userResults.value);
  const attemptedCount = results.length;

  // 2. Failsafe for 0 questions
  if (attemptedCount === 0) return 0;

  // 3. Count the exact number of correct answers and calculate
  const correctAnswers = results.filter(result => result === true).length;
  return Math.round((correctAnswers / attemptedCount) * 100);
});

// 3. Determine if they passed the AWS standard (72%)
const isPassing = computed(() => scorePercentage.value >= 72);

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
    // 1. Clear old errors
    authValidationMessage.value = "";
    authError.value = '';

    // 🚨 THE INTERCEPTOR: Block the request BEFORE it goes to Java!
    if (!isLoginMode.value) {
      if (authForm.value.email !== authForm.value.confirmEmail) {
        authValidationMessage.value = "Emails do not match. Please verify.";
        return; // Stops the function immediately
      }
      if (authForm.value.password !== authForm.value.confirmPassword) {
        authValidationMessage.value = "Passwords do not match. Please verify.";
        return; // Stops the function immediately
      }
    }

    try {
      const endpoint = isLoginMode.value ? '/api/auth/login' : '/api/users/register';
      console.log(`Initiating ${isLoginMode.value ? 'Login' : 'Registration'} Protocol...`);

      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(authForm.value)
      });

      if (response.ok) {
        if (!isLoginMode.value) {
          // 🚨 REGISTRATION SUCCESS: Trigger the Welcome Modal
          showAuthModal.value = false;
          showWelcomeModal.value = true;

          // Wait 2.5 seconds for dramatic effect, then Auto-Login
          setTimeout(async () => {
            showWelcomeModal.value = false;

            const autoLoginResponse = await fetch(`${API_BASE_URL}/api/auth/login`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                email: authForm.value.email,
                password: authForm.value.password
              })
            });

            if (autoLoginResponse.ok) {
              const fullUserObj = await autoLoginResponse.json();
              completeLoginProcess(fullUserObj); // Hands off to your function!
            }
          }, 2500);

        } else {
          // Normal Login Success
          const fullUserObj = await response.json();
          completeLoginProcess(fullUserObj);
        }
      } else {
        // Error Handling
        if (response.status === 403 || response.status === 401) {
          authForm.value.password = '';
          authError.value = "Access Denied: Invalid username or passcode.";
        } else if (response.status === 400) {
          const errorText = await response.text();
          authError.value = errorText;
        } else {
          authError.value = "Matrix Error: The authentication server is unreachable.";
        }
      }
    } catch (error) {
      console.error("Authentication pipeline severed:", error);
      authError.value = "Critical Error: Could not reach the authentication server.";
    }
  };

const completeLoginProcess = (loginData) => {
    // 1. Save the VIP Pass
    const token = loginData.token || loginData.jwt;
    localStorage.setItem('aws_jwt', token);

    // 2. Save the User Data
    currentUser.value = loginData;
    localStorage.setItem('aws_user', JSON.stringify(loginData));

    // 3. Close modal and route
    showAuthModal.value = false;
    currentView.value = 'landing';
    authForm.value = { username: '', email: '', password: '' };

    // Debugging log so we can see exactly what Java gave us!
    console.log("LOGIN COMPLETE. User Data from Java:", loginData);
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
          showToast(errorData.error || "Payment error.", "error");
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
    showUpgradePrompt.value = false; // Hide the upgrade prompt so it gets out of the way

    try {
      const token = localStorage.getItem('aws_jwt');

      // 🚨 THE GUEST TRAPDOOR: If no token, slide open the Auth modal
      if (!token) {
        localStorage.setItem('aws_pending_checkout', 'true');
        showAuthModal.value = true;
        isLoginMode.value = false; // Force it to the "Sign Up" tab
        return;
      }

      // 🚨 THE FIX: Route them to the Legal Agreement instead of directly to Stripe!
      pendingPlanType.value = 'monthly'; // Or whatever default plan you want them to buy
      hasAcceptedTerms.value = false;    // Force them to check the box
      showAgreementModal.value = true;   // Show the legal terminal

    } catch (error) {
      console.error("Payment routing error:", error);
    }
  };

// 3. Catches the pricing button click and routes to the Legal Terminal
const prepareCheckout = (planType) => {
    const token = localStorage.getItem('aws_jwt');

    // 🚨 The Seamless Guest Handoff
    if (!token) {
      showPremiumModal.value = false;
      showAuthModal.value = true;
      isLoginMode.value = false;
      return;
    }

    // 🚨 THE FIX: Route to the Legal Agreement instead of Stripe!
    pendingPlanType.value = planType;  // Save which button they clicked
    hasAcceptedTerms.value = false;    // Force them to check the box
    showAgreementModal.value = true;   // Pop the legal terminal
  };

const logout = () => {
  localStorage.removeItem('aws_jwt');
  localStorage.removeItem('aws_user');
  authToken.value = null;
  currentUser.value = null;
};

// The Cancellation Function
const processCancellation = async () => {
    if (!legalAgreed.value) {
      cancelFeedback.value = "You must agree to the legal terms to proceed.";
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/api/users/cancel-premium`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('aws_jwt')}`
        }
      });

      const data = await response.json();
      if (response.ok) {
        cancelFeedback.value = `✅ Successfully scheduled. Your premium access remains active until ${data.expirationDate}.`;
        // Optionally hide the form and just show the success message
        setTimeout(() => { showCancelModal.value = false; }, 4000);
      } else {
        cancelFeedback.value = "❌ " + data.error;
      }
    } catch (error) {
      cancelFeedback.value = "❌ Critical Error: Could not reach the Matrix.";
    }
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
      const returnCertStr = localStorage.getItem('aws_return_cert');

      if (returnView && returnView !== 'undefined') currentView.value = returnView;

      // SAFETY SHIELD: Safely parse the returning cert
      if (returnCertStr && returnCertStr !== 'undefined') {
        try { selectedCert.value = JSON.parse(returnCertStr); } catch(e) {}
      }

      welcomeMessage.value = "Payment Secured! Premium Databanks Unlocked.";
      showSuccessHologram.value = true;
      setTimeout(() => { showSuccessHologram.value = false; }, 4500);

      const actualToken = localStorage.getItem('aws_jwt');
      const savedUserStr = localStorage.getItem('aws_user');

      // SAFETY SHIELD: Safely parse the user
      if (savedUserStr && savedUserStr !== 'undefined' && actualToken) {
        try {
          const savedUser = JSON.parse(savedUserStr);
          savedUser.isPremium = true;
          currentUser.value = savedUser;
          localStorage.setItem('aws_user', JSON.stringify(savedUser));

          fetch(`${API_BASE_URL}/api/users/${savedUser.username}`, {
            headers: { 'Authorization': `Bearer ${actualToken}` }
          })
              .then(res => res.ok ? res.json() : null)
              .then(updatedUser => {
                if (updatedUser) {
                  // 🛡️ RACE CONDITION SHIELD 1: Webhook is probably lagging. Force it!
                  updatedUser.isPremium = true;

                  currentUser.value = updatedUser;
                  localStorage.setItem('aws_user', JSON.stringify(updatedUser));
                }
              })
              .catch(err => console.warn("Local Premium Override Active."));

          const intendedProtocol = localStorage.getItem('aws_intended_protocol');
          if (intendedProtocol && intendedProtocol !== 'undefined') {
            setTimeout(() => {
              bootQuizEngine(intendedProtocol);
              localStorage.removeItem('aws_intended_protocol');
            }, 4500);
          } else if (returnView && returnView !== 'undefined') {
            currentView.value = returnView;
          }
        } catch (error) {
          console.error("Corrupted Stripe user memory wiped.", error);
          localStorage.removeItem('aws_user');
        }
      }

      window.history.replaceState({}, document.title, window.location.pathname);
      localStorage.removeItem('aws_awaiting_stripe_return');
      localStorage.removeItem('aws_return_view');
      localStorage.removeItem('aws_return_cert');

    } else if (urlParams.get('canceled') === 'true') {
      showToast("Payment canceled. You have not been charged.", "info");
      window.history.replaceState({}, document.title, window.location.pathname);
    } else if (localStorage.getItem('aws_awaiting_stripe_return') === 'true') {
      const savedView = localStorage.getItem('aws_return_view');
      if (savedView && savedView !== 'undefined') currentView.value = savedView;
    }

    history.replaceState({ view: currentView.value }, '', `#${currentView.value}`);

    // 🚨 3. THE MEMORY RESTORE
    const savedCertCode = localStorage.getItem('aws_active_cert');
    if (savedCertCode && savedCertCode !== 'undefined' && certifications.value) {
      const foundCert = certifications.value.find(c => c.code === savedCertCode);
      if (foundCert) {
        selectedCert.value = foundCert;
        const savedDomainName = localStorage.getItem('aws_active_domain');
        if (savedDomainName && savedDomainName !== 'undefined' && foundCert.domains) {
          const foundDomain = foundCert.domains.find(d => d.name === savedDomainName);
          if (foundDomain) {
            activeDomain.value = foundDomain;
            const savedModuleId = localStorage.getItem('aws_active_module');
            if (savedModuleId && savedModuleId !== 'undefined' && foundDomain.modules) {
              const foundModule = foundDomain.modules.find(m => m.id === savedModuleId);
              if (foundModule) activeModule.value = foundModule;
            }
          }
        }
      }
    }

    if (typeof fetchHallOfFame === 'function') fetchHallOfFame();
    if (typeof fetchLeaderboard === 'function') fetchLeaderboard();

    // 🚨 5. THE CRASH ZONE FIX: Check the browser's permanent memory
    const returningUserStr = localStorage.getItem('aws_user');

    if (returningUserStr && returningUserStr !== 'undefined') {
      try {
        // 1. Load what we have immediately for a fast UI
        const localUser = JSON.parse(returningUserStr);
        currentUser.value = localUser;
        showAuthModal.value = false;

        // 🚨 THE MASTER SYNC: Ask Java for your true database profile (which contains your official ID!)
        const actualToken = localStorage.getItem('aws_jwt');
        if (actualToken && localUser.username) {
          fetch(`${API_BASE_URL}/api/users/${localUser.username}`, {
            headers: { 'Authorization': `Bearer ${actualToken}` }
          })
              .then(res => res.ok ? res.json() : null)
              .then(dbUser => {
                if (dbUser) {
                  // 🛡️ RACE CONDITION SHIELD 2: Never let a lagging backend downgrade a Premium user!
                  if (localUser.isPremium === true) {
                    dbUser.isPremium = true;
                  }

                  // 2. Save the true profile (with the ID) to Vue and Local Storage
                  currentUser.value = dbUser;
                  localStorage.setItem('aws_user', JSON.stringify(dbUser));

                  console.log("✅ Identity Synced with Mainframe. User ID:", dbUser.id);

                  // 3. NOW that we have the official ID, fire the data bridges!
                  if (typeof fetchHistory === 'function') fetchHistory();
                  if (typeof fetchRadarStats === 'function') fetchRadarStats();
                }
              })
              .catch(err => console.error("Mainframe sync failed:", err));
        }

        // Load the public data
        if (typeof fetchAdminQuestions === 'function') fetchAdminQuestions();
        if (typeof fetchAdminFlashcards === 'function') fetchAdminFlashcards();
        if (typeof fetchAdminPipelines === 'function') fetchAdminPipelines();
        if (selectedCert.value && typeof fetchQuestion === 'function') fetchQuestion();

      } catch (error) {
        console.error("Corrupt user memory detected. Wiping local storage.");
        localStorage.removeItem('aws_user');
      }
    } else {
      if (returningUserStr === 'undefined') {
        localStorage.removeItem('aws_user');
      }
    }
  });

watch(currentView, (newView) => {
  // Only push to history if it's actually a new screen
  if (history.state === null || history.state.view !== newView) {
    history.pushState({ view: newView }, '', `#${newView}`);
  }
});

//Hijack the Browser's Back/Forward Arrows
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
/* Toast notification transitions */
.toast-slide-enter-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-slide-leave-active { transition: all 0.3s ease-in; }
.toast-slide-enter-from { opacity: 0; transform: translate(-50%, -20px) scale(0.95); }
.toast-slide-leave-to { opacity: 0; transform: translate(-50%, -10px) scale(0.98); }

@keyframes pulse-once {
  0%, 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4); }
  50% { box-shadow: 0 0 20px 4px rgba(239, 68, 68, 0.2); }
}
.animate-pulse-once { animation: pulse-once 1s ease-in-out; }

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

/* ⚖️ LEGAL TERMINAL SCROLLBAR */
.custom-scrollbar {
  scrollbar-width: thin; /* Firefox */
  scrollbar-color: #334155 #0f172a; /* Firefox thumb and track */
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px; /* Chrome, Edge, Safari */
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #0f172a; /* Tailwind slate-950 */
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #334155; /* Tailwind slate-700 */
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #475569; /* Tailwind slate-600 */
}

</style>



