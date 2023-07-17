export const appConstants = {
    emailPattern: '^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$',
    localStorageKeys: {
        userFeatures: 'userFeatures',
        accessToken: 'access_token',
        accessTokenExpiryTime: 'access_token_expiry_time',
        refreshToken: 'refresh_token',
        refreshTokenExpiryTime: 'refresh_token_expiry_time',
        language: 'language',
        Username: 'Username',
        Firstname: 'Firstname',
        Lastname: 'Lastname',
        ApplicationAgent: 'ApplicationAgent',
        FacilityName: 'FacilityName'
    },
    EMailAsUsername: "REGISTRATION_EMAIL_AS_USERNAME",
    genderArr: [
        { id: 'male', name: 'Male' },
        { id: 'female', name: 'Female' },
        { id: 'other', name: 'Other' }
    ],
    conditionArrForAgeAndColor: [
        { id: '<', name: '< (less than)' },
        { id: '>', name: '> (greater than)' },
        { id: '<=', name: '<= (less than equal to)' },
        { id: '>=', name: '>= (greater than equal to)' }
    ]
};
