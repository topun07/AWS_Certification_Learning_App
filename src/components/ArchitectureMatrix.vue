<template>
  <div class="w-full bg-slate-50/50 rounded-3xl p-6 2xl:p-10 border border-slate-200 shadow-sm min-h-[500px]">

    <div v-if="currentScenario">

      <div class="mb-8">
        <h2 class="text-2xl 2xl:text-4xl font-black text-slate-800 mb-2">The Architecture Matrix</h2>
        <p class="text-slate-600 text-sm 2xl:text-lg font-medium p-4 bg-white rounded-xl border border-slate-200 shadow-inner">
          <span class="font-bold text-pink-600 uppercase tracking-wider text-xs">Mission Briefing:</span><br/>
          {{ currentScenario.description }}
        </p>
      </div>

      <div class="mb-8 flex flex-col items-start gap-2 w-full">
        <button
            v-if="hintLevel < 3"
            @click="hintLevel++"
            class="text-xs font-bold text-indigo-500 bg-indigo-50 px-3 py-1 rounded-md border border-indigo-200 hover:bg-indigo-100 transition-colors flex items-center gap-2 shadow-sm"
        >
          <span>💡</span> {{ hintLevel === 0 ? 'Need a Hint?' : hintLevel === 1 ? 'Need Another Hint?' : '🚨 Final Hint!' }}
        </button>
        <button
            v-else
            @click="hintLevel = 0"
            class="text-xs font-bold text-slate-500 bg-slate-50 px-3 py-1 rounded-md border border-slate-200 hover:bg-slate-100 transition-colors flex items-center gap-2 shadow-sm"
        >
          <span>👀</span> Hide Hints
        </button>

        <div v-if="hintLevel >= 1" class="p-3 bg-indigo-50 border-l-4 border-indigo-400 text-indigo-700 text-sm font-medium rounded-r-lg animate-fade-in w-full shadow-sm">
          <span class="font-black uppercase tracking-widest text-[10px] opacity-70 block mb-1">Hint 1</span>
          This architecture requires exactly <span class="font-black text-pink-600">{{ currentScenario.solution.length }}</span> services to complete.
        </div>

        <div v-if="hintLevel >= 2" class="p-3 bg-indigo-50 border-l-4 border-indigo-400 text-indigo-700 text-sm font-medium rounded-r-lg animate-fade-in w-full shadow-sm">
          <span class="font-black uppercase tracking-widest text-[10px] opacity-70 block mb-1">Hint 2</span>
          {{ currentScenario.hint }}
        </div>

        <div v-if="hintLevel >= 3" class="p-3 bg-indigo-50 border-l-4 border-indigo-400 text-indigo-700 text-sm font-medium rounded-r-lg animate-fade-in w-full shadow-sm">
          <span class="font-black uppercase tracking-widest text-[10px] opacity-70 block mb-1">Hint 3</span>
          {{ currentScenario.hint2 }}
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 2xl:gap-12">

        <div class="bg-slate-100 rounded-2xl p-6 border-2 border-slate-200 border-dashed">
          <h3 class="text-lg 2xl:text-xl font-black text-slate-400 mb-4 uppercase tracking-widest text-center">Toolbox</h3>
          <draggable
              v-model="toolbox"
              group="pipeline-builder"
              item-key="name"
              class="flex flex-col gap-3 min-h-[250px]"
              ghost-class="opacity-50"
          >
            <template #item="{ element }">
              <div class="bg-white border-2 border-slate-200 text-slate-700 font-bold p-4 2xl:p-5 rounded-xl cursor-grab active:cursor-grabbing hover:border-pink-400 hover:shadow-md transition-all flex items-center gap-3">
                <span class="text-xl">⚙️</span>
                {{ element.name }}
              </div>
            </template>
          </draggable>
        </div>

        <div class="bg-pink-50 rounded-2xl p-6 border-2 border-pink-200">
          <h3 class="text-lg 2xl:text-xl font-black text-pink-500 mb-4 uppercase tracking-widest text-center">Your Pipeline</h3>
          <draggable
              v-model="pipeline"
              group="pipeline-builder"
              item-key="name"
              class="flex flex-col gap-3 min-h-[250px]"
              ghost-class="opacity-50"
          >
            <template #item="{ element, index }">
              <div class="bg-pink-600 border-2 border-pink-700 text-white font-bold p-4 2xl:p-5 rounded-xl cursor-grab active:cursor-grabbing shadow-lg flex items-center gap-3 relative">
                <span class="bg-white text-pink-600 w-6 h-6 rounded-full flex items-center justify-center text-xs font-black absolute -left-3 -top-3 shadow-sm border-2 border-pink-200">
                  {{ index + 1 }}
                </span>
                {{ element.name }}
              </div>
            </template>
          </draggable>
          <div v-if="pipeline.length === 0" class="h-full flex items-center justify-center text-pink-300 font-bold text-sm 2xl:text-base mt-10">
            Drag services here to build the architecture.
          </div>
        </div>
      </div>

      <div class="mt-8 flex flex-col items-center gap-4">
        <button
            v-if="!isSuccess"
            @click="verifyPipeline"
            class="bg-slate-800 hover:bg-slate-900 text-white font-black py-3 px-8 2xl:py-4 2xl:px-12 rounded-xl transition-all shadow-lg hover:shadow-xl"
        >
          DEPLOY PIPELINE
        </button>

        <p v-if="feedbackMessage && !isSuccess" class="text-red-500 font-bold text-lg text-center animate-pulse">
          {{ feedbackMessage }}
        </p>

        <div v-if="isSuccess" class="w-full bg-emerald-50 border-2 border-emerald-400 rounded-2xl p-6 text-center animate-fade-in shadow-lg">
          <h3 class="text-2xl font-black text-emerald-600 mb-2">✅ Architecture Verified!</h3>
          <p class="text-emerald-800 font-medium text-sm md:text-base leading-relaxed mb-6">
            {{ currentScenario.explanation }}
          </p>
          <button
              @click="fetchScenario"
              class="bg-emerald-500 hover:bg-emerald-600 text-white font-bold py-3 px-8 rounded-xl transition-all shadow-md active:scale-95"
          >
            LOAD NEXT MISSION ➔
          </button>
        </div>
      </div>
    </div>

    <div v-else class="h-full flex flex-col items-center justify-center py-20 text-slate-500">
      <span class="text-5xl mb-4 animate-bounce">⚡</span>
      <h2 class="text-xl 2xl:text-2xl font-bold animate-pulse uppercase tracking-widest">Connecting to Matrix Databanks...</h2>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import draggable from 'vuedraggable';

// 1. Data defaults to null so the loading screen shows first
const currentScenario = ref(null);
const toolbox = ref([]);
const pipeline = ref([]);
const feedbackMessage = ref("");
const isSuccess = ref(false);
const hintLevel = ref(0);

// 2. The Fetch Protocol
const fetchScenario = async () => {
  try {
    // Reset the board immediately when fetching a new mission
    currentScenario.value = null;

    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/questions/pipeline/random?examCode=AIF-C01`);
    if (response.ok) {
      const data = await response.json();
      currentScenario.value = {
        description: data.scenarioDescription,
        solution: data.correctPipelineOrder,
        hint: data.hint,
        hint2: data.hint2,
        explanation: data.explanation
      };

      // Reset game state
      hintLevel.value = 0;
      isSuccess.value = false;
      feedbackMessage.value = "";
      pipeline.value = [];

      // Shuffle the toolbox
      toolbox.value = data.toolboxServices
          .map(name => ({ name }))
          .sort(() => Math.random() - 0.5);
    } else {
      console.error("Database connection failed or table is empty.");
    }
  } catch (error) {
    console.error("Failed to connect to the Matrix:", error);
  }
};

// 3. The Verification Logic
const verifyPipeline = () => {
  if (!currentScenario.value) return;

  const userOrder = pipeline.value.map(item => item.name);

  if (userOrder.length !== currentScenario.value.solution.length) {
    isSuccess.value = false;
    feedbackMessage.value = "Pipeline Failed: Incorrect number of services deployed.";
    return;
  }

  for (let i = 0; i < userOrder.length; i++) {
    if (userOrder[i] !== currentScenario.value.solution[i]) {
      isSuccess.value = false;
      feedbackMessage.value = `Pipeline Crash: ${userOrder[i]} is in the wrong position.`;
      return;
    }
  }

  isSuccess.value = true;
  feedbackMessage.value = ""; // Clear error message on success
};

// 4. Run fetch immediately when the component loads
onMounted(() => {
  fetchScenario();
});
</script>