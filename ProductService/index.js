import express from 'express';
import cors from 'cors';
import { ConnectDB, CreateSeedData } from './db/products.js';
import axios from 'axios';
import ProductRouter from './routers/productrouter.js'
import { config } from './config.js'

const app = express();
app.disable('x-powered-by');
app.use(cors())
app.use(express.json());

app.use('/products', ProductRouter);

app.get('/', (req, res) => {
    res.send("hello from home")
});

app.get('/server', async (req, res) => {
    try {
        const response = await axios.get(config.testServer);
        console.log(response)
        res.json(response.data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});
app.use((err, req, res, next) => {
    res.status(500).json({
        success: false,
        message: err.message
    });
})

await ConnectDB();
await CreateSeedData();

app.listen(8080, () => console.log("Server listening on port 8080"))