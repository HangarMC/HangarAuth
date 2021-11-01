import { NextFunction, Request, Response } from 'express';

export default (req: Request, res: Response, next: NextFunction) => {
    const origin = req.headers.origin;
    if (origin && ['http://localhost:3000', 'https://hangar.benndorf.dev'].includes(origin)) {
        res.setHeader('Access-Control-Allow-Origin', origin);
        res.setHeader('Access-Control-Allow-Credentials', 'true');
    }

    next();
};
