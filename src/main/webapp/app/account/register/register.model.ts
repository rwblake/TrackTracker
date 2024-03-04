export class Registration {
  constructor(
    public login: string,
    public email: string,
    public password: string,
    public bio: string | null,
    public spotifyAuthCode: string,
    public spotifyAuthState: string,
    public langKey: string
  ) {}
}
