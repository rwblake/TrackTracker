export class Registration {
  constructor(
    public firstName: string,
    public lastName: string,
    public login: string,
    public email: string,
    public password: string,
    public spotifyAuthCode: string,
    public spotifyAuthState: string,
    public langKey: string
  ) {}
}
