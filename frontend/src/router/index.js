// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        component: () => import('../views/Layout.vue'),
        children: [
            {
                path: '',
                name: 'AreaDescription',
                component: () => import('../views/AreaDescription.vue')
            },
            {
                path: 'map',
                name: 'CustomMap',
                component: () => import('../views/CustomMap.vue')
            },
            {
                path: 'point',
                name: 'PointImage',
                component: () => import('../views/PointImage.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
