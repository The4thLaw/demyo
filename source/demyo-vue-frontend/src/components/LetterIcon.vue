<template>
    <!-- Inspired from vue-letter-avatar but with a customizable color -->
    <svg :style="'width: 24px; height: 24px; border-radius: 100%; background: ' + color + ' none repeat scroll 0% 0%;'">
        <text x="50%" y="55%" text-anchor="middle" dominant-baseline="central"
            style="fill: rgba(255, 255, 255, 0.7); font-family: sans-serif; font-size: 15.6px;">
            {{ letter }}
        </text>
    </svg>
</template>

<script>
export default {
    name: 'LetterIcon',

    props: {
        letter: {
            type: String,
            required: true
        },
        color: {
            type: String,
            required: true
        }
    }
    
}
</script>