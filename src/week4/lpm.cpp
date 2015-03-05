#include <stdio.h>

void read_routes(void);
void read_lookup(void);
void ip2human(unsigned int ip);
int lookup_ip(unsigned int ip);
void add_route(unsigned int p, int prefix_length, int port_number);

int main(void) {
	// TODO: Request output token from student assistent
	puts("s1234567_abcde");
	fflush(stdout);

	read_routes();
	read_lookup();

	return 0;
}

void add_route(unsigned int ip, int prefix_length, int port_number) {
	// TODO: Store route to be used from lookup_ip function
}

int lookup_ip(unsigned int ip) {
	// TODO: Lookup IP in stored data from add_route function,
	//       returns port number (or -1 for no route found).
	return -1;
}

void ip2human(unsigned int ip) {
	unsigned int a, b, c, d;

	a = (ip >> 24) & 0xff;
	b = (ip >> 16) & 0xff;
	c = (ip >>  8) & 0xff;
	d =  ip        & 0xff;

	printf("%i.%i.%i.%i\n", a, b, c, d);
}

void read_routes(void) {
	FILE *f = fopen("routes.txt", "r");
	char s[27];

	while (fgets(s, 27, f)) {
		unsigned int a, b, c, d, ip;
		int pl, pn;

		sscanf(s, "%i.%i.%i.%i/%i %i", &a, &b, &c, &d, &pl, &pn);
		ip = (a<<24) | (b<<16) | (c<<8) | d;

		add_route(ip, pl, pn);
	}

	fclose(f);
}

void read_lookup(void) {
	FILE *f = fopen("lookup.txt", "r");
	char s[17];

	while (fgets(s, 17, f)) {
		unsigned int a, b, c, d, ip;
		int pn;

		sscanf(s, "%i.%i.%i.%i", &a, &b, &c, &d);
		ip = (a<<24) | (b<<16) | (c<<8) | d;

		pn = lookup_ip(ip);
		printf("%i\n", pn);
	}

	fclose(f);
}