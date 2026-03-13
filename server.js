const express = require('express'); // You may need to run 'npm install express'
const app = express();
const PORT = 3000;

app.get('/', (req, res) => {
  res.send('AWS Machine Learning API is running!');
});

app.listen(PORT, () => {
  console.log(`Backend server is spinning at http://localhost:${PORT}`);
});