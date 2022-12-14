<template>

    <v-card outlined>
        <v-card-title>
            VacationDaysStatus # {{$route.params.id }}
        </v-card-title>

        <v-card-text>
            <div>
                <String label="UserId" v-model="item.userId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Number label="DaysLeft" v-model="item.daysLeft" :editMode="editMode" @change="change" />
            </div>
        </v-card-text>
    </v-card>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'VacationDaysStatusViewDetail',
    props: {
      value: Object,
    },
    data: () => ({
        item : [],
    }),
    async created() {
      var params = this.$route.params;
      var temp = await axios.get(axios.fixUrl('/vacationDaysStatuses/' + params.id))

      this.item = temp.data;

    },
    methods: {
    }
  }
</script>

