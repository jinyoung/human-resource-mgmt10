
<template>

    <v-data-table
        :headers="headers"
        :items="vacationDaysStatus"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'VacationDaysStatusView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "userId", value: "userId" },
                { text: "daysLeft", value: "daysLeft" },
            ],
            vacationDaysStatus : [],
        }),
        async created() {
            var me = this;
            setInterval(function(){me.load()}, 1000);
        },
        methods: {
            async load() {
                var temp = await axios.get(axios.fixUrl('/vacationDaysStatuses'))

                temp.data._embedded.vacationDaysStatuses.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

                this.vacationDaysStatus = temp.data._embedded.vacationDaysStatuses;
            }
        }
    }
</script>

