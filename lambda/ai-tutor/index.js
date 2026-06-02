const { BedrockRuntimeClient, InvokeModelCommand } = require("@aws-sdk/client-bedrock-runtime");

const client = new BedrockRuntimeClient({ region: "us-east-1" });

exports.handler = async (event) => {
  const headers = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type, Authorization"
  };

  // Handle CORS preflight
  if (event.httpMethod === "OPTIONS") {
    return { statusCode: 200, headers, body: "" };
  }

  try {
    const body = JSON.parse(event.body);
    const { questionText, userAnswer, correctAnswer, explanation, certCode, followUp, conversationContext } = body;

    let prompt;

    if (followUp && conversationContext) {
      prompt = `You are an AWS certification tutor. A student is asking a follow-up question about an exam question they got wrong on the ${certCode} exam. Be concise and educational. Use 3-4 sentences max.

ORIGINAL QUESTION: ${questionText}
CORRECT ANSWER: ${correctAnswer}

PREVIOUS CONVERSATION:
${conversationContext}

Answer the student's latest follow-up question directly and clearly. Stay focused on the AWS concept.`;
    } else {
      prompt = `You are an AWS certification tutor helping a student who got a question wrong on the ${certCode} exam. Be concise, friendly, and educational. Use 3-5 sentences max.

QUESTION: ${questionText}

STUDENT'S WRONG ANSWER: ${userAnswer}

CORRECT ANSWER: ${correctAnswer}

EXISTING EXPLANATION: ${explanation}

Explain WHY their answer is wrong and WHY the correct answer is right. Use a real-world analogy if helpful. Don't repeat the explanation verbatim - add new insight.`;
    }

    const command = new InvokeModelCommand({
      modelId: "us.amazon.nova-micro-v1:0",
      contentType: "application/json",
      body: JSON.stringify({
        messages: [{ role: "user", content: [{ text: prompt }] }],
        inferenceConfig: { maxTokens: 300, temperature: 0.7 }
      })
    });

    const response = await client.send(command);
    const result = JSON.parse(new TextDecoder().decode(response.body));
    const aiResponse = result.output.message.content[0].text;

    return {
      statusCode: 200,
      headers,
      body: JSON.stringify({ response: aiResponse })
    };

  } catch (error) {
    console.error("Bedrock Error:", error);
    return {
      statusCode: 200,
      headers,
      body: JSON.stringify({ response: "AI Tutor is temporarily unavailable. Review the explanation above and focus on understanding the underlying AWS service behavior." })
    };
  }
};
