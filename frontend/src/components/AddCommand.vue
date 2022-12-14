<template>

    <v-card outlined>
        <v-card-title>
            Add
        </v-card-title>

        <v-card-text>
            <Number label="DayCount" v-model="value.dayCount" :editMode="editMode"/>
            <String label="Reason" v-model="value.reason" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="add"
            >
                Add
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>
   
    export default {
        name: 'AddCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.dayCount = 0;
            this.value.reason = '';
        },
        watch: {
        },
        methods: {
            add() {
                this.$emit('add', this.value);
            },
            close() {
                this.$emit('closeDialog');
            },
            change() {
                this.$emit('input', this.value);
            },
        }
    }
</script>

