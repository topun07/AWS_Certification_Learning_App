<template>
  <div id="app">
    <QuestionComponent
        :isLoggedIn="isLoggedIn"
        :isPremium="isPremium"
        :username="username"
    />
  </div>
</template>

<script setup>
// 1. We added 'onMounted' and 'ref' to your Vue imports
import { onMounted, ref } from 'vue'
import QuestionComponent from './components/QuestionComponent.vue'

// 2. Set up our state variables
const isLoggedIn = ref(false);
const isPremium = ref(false);
const username = ref('');

// 3. Grab the API URL from your .env file
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// 4. The Startup Check: Runs the millisecond the app loads (or returns from Stripe)
onMounted(async () => {
  const token = localStorage.getItem('aws_jwt');

  if (token) {
    // Optimistic login: We know they have a token, assume they are logged in while we verify
    isLoggedIn.value = true;

    // 🚨 THE RACE CONDITION SHIELD: Give Stripe 3 seconds to update Java!
    if (localStorage.getItem('aws_awaiting_stripe_return') === 'true') {
      console.log("⏳ Awaiting Stripe Webhook synchronization...");
      // Pause Vue execution for exactly 3 seconds
      await new Promise(resolve => setTimeout(resolve, 3000));
    }

    // Now it is safe to ask Java for the profile!
    try {
      const response = await fetch(`${API_BASE_URL}/api/users/profile`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        const userData = await response.json();

        // Note: If you fully transitioned to Email instead of Username,
        // you might want to map this to userData.email depending on your Java backend!
        username.value = userData.username;
        isPremium.value = userData.isPremium; // Reads their new premium status!

        console.log(`✅ Mainframe Connection Established. Welcome back, ${username.value}.`);
      } else {
        console.warn("Token expired or invalid. Purging access.");
        localStorage.removeItem('aws_jwt');
        isLoggedIn.value = false;
      }
    } catch (error) {
      console.error("Matrix connection failed on startup:", error);
    }
  }
});
</script>

<style>
body {
  background-color: #f3f4f6;
  margin: 0;
}
</style>